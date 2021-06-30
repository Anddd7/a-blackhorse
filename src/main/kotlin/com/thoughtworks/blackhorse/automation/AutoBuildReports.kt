package com.thoughtworks.blackhorse.automation

import com.thoughtworks.blackhorse.reporter.PerformancePrinter
import com.thoughtworks.blackhorse.utils.extractProjectName
import org.slf4j.Logger
import org.slf4j.LoggerFactory

object AutoBuildReports {
    private val log: Logger = LoggerFactory.getLogger(this.javaClass)

    fun build(files: List<String>) {
        val projectNames = filterProjects(files)
        logBeforeRebuild()
        PerformancePrinter.printReports(projectNames)
        logAfterRebuild()
    }

    private fun logAfterRebuild() {
        log.info("-------------------------------------------------------")
        log.info("| --> Done Automation Build Reports")
        log.info("-------------------------------------------------------")
    }

    private fun logBeforeRebuild() {
        log.info("-------------------------------------------------------")
        log.info("| --> Start Automation Build Reports")
        log.info("-------------------------------------------------------")
    }

    private fun filterProjects(files: List<String>) =
        files.map(String::extractProjectName).toSet()
}
