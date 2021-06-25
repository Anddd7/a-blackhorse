package com.thoughtworks.blackhorse.automation

import com.thoughtworks.blackhorse.reporter.PerformancePrinter
import com.thoughtworks.blackhorse.utils.extractProjectName

object AutoBuildReports {
    fun build(files: List<String>) {
        val projectNames = filterProjects(files)
        logBeforeRebuild()
        projectNames.forEach(PerformancePrinter::rebuildProjectReport)
        logAfterRebuild()
    }

    private fun logAfterRebuild() {
        println(
            """
            | --> Done Automation Build Reports
            -------------------------------------------------------
            """.trimIndent()
        )
    }

    private fun logBeforeRebuild() {
        println(
            """
            -------------------------------------------------------
            | --> Start Automation Build Reports
            """.trimIndent()
        )
    }

    private fun filterProjects(files: List<String>) =
        files.map(String::extractProjectName).toSet()
}
