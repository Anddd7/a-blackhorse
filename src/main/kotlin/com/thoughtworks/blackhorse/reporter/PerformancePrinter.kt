package com.thoughtworks.blackhorse.reporter

import com.thoughtworks.blackhorse.config.GlobalConfig
import com.thoughtworks.blackhorse.config.ProjectConfig
import com.thoughtworks.blackhorse.config.ProjectConfig.Companion.getProjectFile
import com.thoughtworks.blackhorse.reporter.formater.MarkdownPerformanceFormatter
import com.thoughtworks.blackhorse.schema.performance.StoryPerformance
import com.thoughtworks.blackhorse.schema.story.StoryOf
import com.thoughtworks.blackhorse.schema.story.evaluate
import com.thoughtworks.blackhorse.utils.findStories
import java.nio.file.Files

object PerformancePrinter {
    fun printReports() {
        GlobalConfig.projects.keys.forEach { rebuildProjectReport(it) }
    }

    fun rebuildProjectReport(projectName: String) {
        ProjectConfig.execute(projectName) {
            val rawData = getPerformances(projectName)
            val content = MarkdownPerformanceFormatter.performances(rawData)
            val file = getProjectFile("performance-report.md")
            Files.writeString(file, content)
        }
    }

    private fun getPerformances(projectName: String) =
        findStories(projectName).mapNotNull(StoryOf::evaluate).sortedBy(StoryPerformance::startAt)
}

fun main() {
    PerformancePrinter.printReports()
}
