package com.thoughtworks.blackhorse.config

import com.thoughtworks.blackhorse.printer.PrinterOption
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import java.util.Properties

data class ProjectConfig(
    private val projectName: String,
    private val properties: Properties,
) {
    private fun getProperty(key: String): String? =
        properties.getProperty(key)

    private fun getPropertyOrThrow(key: String): String =
        getProperty(key) ?: throw IllegalArgumentException("{$key} is missing in properties")

    private fun getPropertyOrDefault(key: String, default: String): String =
        getProperty(key) ?: default

    private val hiddenOptions = properties.getProperty("hidden")
        ?.split(",")
        ?.map { it.trim().uppercase() }
        ?.map(HiddenOption::valueOf)
        ?: emptyList()

    private fun isVisible(hiddenOption: HiddenOption): Boolean = hiddenOptions.contains(hiddenOption)
    private fun printerOption() =
        getPropertyOrThrow("printer").uppercase().let(PrinterOption::valueOf)

    private fun printer() = printerOption().printer
    private fun distDir(): Path = Paths.get(getPropertyOrThrow("dist_dir")).resolve(projectName)
    private fun jiraBaseUrl() = getPropertyOrThrow("jira_baseurl")
    private fun jiraToken() = getPropertyOrThrow("jira_token")
    private fun costAlgorithm() =
        getPropertyOrDefault("cost_algorithm", "flow").uppercase().let(CostAlgorithmOption::valueOf)

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
        fun costAlgorithm() = instance().costAlgorithm()
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
