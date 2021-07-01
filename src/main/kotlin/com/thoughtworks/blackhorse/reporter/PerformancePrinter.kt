package com.thoughtworks.blackhorse.reporter

import com.thoughtworks.blackhorse.config.ProjectContext
import com.thoughtworks.blackhorse.config.PropertyLoader
import com.thoughtworks.blackhorse.reporter.formater.MarkdownPerformanceFormatter
import com.thoughtworks.blackhorse.schema.performance.StoryPerformance
import com.thoughtworks.blackhorse.schema.story.StoryOf
import com.thoughtworks.blackhorse.schema.story.infoTime
import com.thoughtworks.blackhorse.utils.findStories
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import org.slf4j.LoggerFactory
import java.nio.file.Files

object PerformancePrinter {
    private val log = LoggerFactory.getLogger(this.javaClass)

    fun printReportsLocally() {
        log.infoTime("Performance Printing") { printReports() }
    }

    fun printReports(projectKeys: Set<String> = PropertyLoader.projects.keys) {
        runBlocking {
            withContext(Dispatchers.IO) {
                projectKeys.map {
                    async { rebuildProjectReport(it) }
                }.awaitAll()
            }
        }
    }

    private fun rebuildProjectReport(projectName: String) {
        log.info("Start > rebuilding performance report for [$projectName]...")

        val context = ProjectContext.load(projectName)
        val rawData = getPerformances(context.projectName)
        val content = MarkdownPerformanceFormatter.performances(rawData)
        val file = context.getProjectFile("performance-report.md")
        Files.writeString(file, content)

        log.info("End < rebuilding performance report for [$projectName]...")
    }

    private fun getPerformances(projectName: String) =
        findStories(projectName).mapNotNull(StoryOf::buildPerformance).sortedBy(StoryPerformance::startAt)
}

fun main() {
    PerformancePrinter.printReportsLocally()
}
