package com.thoughtworks.blackhorse.automation

import com.thoughtworks.blackhorse.utils.ShellCli
import com.thoughtworks.blackhorse.utils.ShellOperation

object AutoBuild {
    fun build(filesFromCI: List<String>) {
        val files = filesFromCI.ifEmpty { findChangedFiles() }
            .filter(this::isProjectFiles)
            .map(this::toClassPath)

        val hasArchitectureChanges = AutoBuildArchitectures.build(files)
        val hasStoriesChanges = hasArchitectureChanges || AutoBuildStories.build(files)
        if (hasStoriesChanges) AutoBuildReports.build(files)
    }

    private fun findChangedFiles(): List<String> {
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
