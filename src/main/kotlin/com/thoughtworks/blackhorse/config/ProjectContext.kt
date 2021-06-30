package com.thoughtworks.blackhorse.config

import com.thoughtworks.blackhorse.printer.PrinterOption
import com.thoughtworks.blackhorse.utils.FileExtension
import com.thoughtworks.blackhorse.utils.toEnum
import java.nio.file.Paths
import java.util.Properties
import java.util.concurrent.ConcurrentHashMap

data class ProjectContext(
    val projectName: String,
    val printerOption: PrinterOption,
    private val _jiraBasUrl: OptionalProperty,
    private val _jiraToken: OptionalProperty,
    private val hiddenOptions: List<HiddenOption>,
) {
    val printer = printerOption.printer
    val distPath = Paths.get("dist").resolve(projectName)
    val jiraBasUrl: String by lazy { _jiraBasUrl.get() }
    val jiraToken: String by lazy { _jiraToken.get() }

    fun isVisible(hiddenOption: HiddenOption) = !hiddenOptions.contains(hiddenOption)
    fun getProjectFile(filename: String) = FileExtension.getOrCreateFile(filename, distPath)

    fun override(printer: PrinterOption?) = copy(
        printerOption = printer ?: printerOption
    )

    companion object {
        private val cache: ConcurrentHashMap<String, ProjectContext> = ConcurrentHashMap()

        fun load(projectName: String): ProjectContext =
            cache.getOrPut(projectName) { PropertyLoader.projects.getValue(projectName).toContext(projectName) }
    }
}

private fun Properties.toContext(projectName: String) = ProjectContext(
    projectName,
    getPropertyOrThrow("printer").toEnum(PrinterOption::valueOf),
    getOptionalProperty("jira_baseurl"),
    getOptionalProperty("jira_token"),
    getProperty("hidden")
        ?.split(",")
        ?.map { it.toEnum(HiddenOption::valueOf) }
        ?: emptyList()
)

private fun Properties.getPropertyOrThrow(key: String): String =
    getProperty(key) ?: throw IllegalArgumentException("{$key} is missing in properties")

private fun Properties.getOptionalProperty(key: String): OptionalProperty =
    OptionalProperty(key, getProperty(key))

class OptionalProperty(private val key: String, private val value: String?) {
    fun get() = value ?: throw IllegalArgumentException("{$key} is missing in properties")
}