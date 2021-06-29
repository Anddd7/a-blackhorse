package com.thoughtworks.blackhorse.config

import com.thoughtworks.blackhorse.printer.PrinterOption
import com.thoughtworks.blackhorse.utils.toEnum
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import java.util.Properties

data class ProjectConfig(
    private val projectName: String,
    private val properties: Properties,
) {
    private fun get(key: String): String? = properties.getProperty(key)
    private fun getOr(key: String, default: String): String = get(key) ?: default
    private fun getOrThrow(key: String): String =
        get(key) ?: throw IllegalArgumentException("{$key} is missing in properties")

    // required
    private fun printerOption() = getOrThrow("printer").toEnum(PrinterOption::valueOf)
    private fun printer() = printerOption().printer
    private fun jiraBaseUrl() = getOrThrow("jira_baseurl")
    private fun jiraToken() = getOrThrow("jira_token")

    // optional
    private val hiddenOptions =
        get("hidden")
            ?.split(",")
            ?.map { it.toEnum(HiddenOption::valueOf) }
            ?: emptyList()

    private fun isVisible(hiddenOption: HiddenOption) = !hiddenOptions.contains(hiddenOption)
    private fun distDir(): Path = Paths.get(getOr("dist_dir", "dist")).resolve(projectName)

    companion object {
        private val current = ThreadLocal<ProjectConfig>()

        private fun load(projectName: String) =
            GlobalConfig.projects[projectName]
                ?.let { ProjectConfig(projectName, it) }
                ?.let(current::set)
                ?: throw IllegalArgumentException("no such project")

        private fun instance() = current.get() ?: throw IllegalAccessException("run ProjectConfig.load() first")

        // quick methods
        private fun projectName() = instance().projectName
        private fun printerOption() = instance().printerOption()
        fun printer() = instance().printer()
        fun distDir() = instance().distDir()
        fun jiraBaseUrl() = instance().jiraBaseUrl()
        fun jiraToken() = instance().jiraToken()
        fun isVisible(hiddenOption: HiddenOption) = instance().isVisible(hiddenOption)
        fun getProjectFile(filename: String) = getOrCreateFile(filename, distDir())

        fun execute(projectName: String, fn: () -> Unit) {
            val startTime = System.currentTimeMillis()
            load(projectName)

            println(
                """
                *******************************************************
                Start Printing
                Project:        ${projectName()}
                Printer:        ${printerOption().name}
                Output Dir:     ${distDir()}
                *******************************************************
                """.trimIndent()
            )

            fn()

            println(
                """
                *******************************************************
                Finished, cost [${System.currentTimeMillis() - startTime}] ms
                *******************************************************
                """.trimIndent()
            )
        }
    }
}

fun getOrCreateFile(filename: String, folder: Path): Path {
    if (Files.notExists(folder)) Files.createDirectories(folder)
    val file = folder.resolve(filename)
    if (Files.notExists(file)) Files.createFile(file)
    return file
}
