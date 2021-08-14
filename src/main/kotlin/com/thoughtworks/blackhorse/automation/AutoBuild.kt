package com.thoughtworks.blackhorse.automation

import com.thoughtworks.blackhorse.schema.story.infoTime
import com.thoughtworks.blackhorse.utils.ShellCli
import com.thoughtworks.blackhorse.utils.ShellOperation
import org.slf4j.Logger
import org.slf4j.LoggerFactory

object AutoBuild {
    private val log: Logger = LoggerFactory.getLogger(this.javaClass)

    fun build(filesFromCI: List<String>) {
        log.infoTime("AutoBuild") {
            val files = filesFromCI.ifEmpty { findChangedFiles() }
                .filter(this::isProjectFiles)
                .map(this::toClassPath)

            AutoBuildArchitectures.build(files)
            val hasStoriesChanges = AutoBuildStories.build(files)
            if (hasStoriesChanges) AutoBuildReports.build(files)
        }
    }

    private fun findChangedFiles(): List<String> {
        log.info("locally rebuild, find changed files from git history")
        var files: List<String> = emptyList()
        ShellOperation.execute(ShellCli.DIFFILE) {
            files = it
        }
        return files
    }

    private fun isProjectFiles(filepath: String) =
        filepath.startsWith("src/main/kotlin/com/thoughtworks/projects")

    private fun toClassPath(filepath: String) =
        filepath.substringAfter("src/main/kotlin/").substringBeforeLast(".kt").split("/").joinToString(".")
}

fun main(args: Array<String>) {
    AutoBuild.build(args.map(String::trim).filter(String::isNotBlank))
}
