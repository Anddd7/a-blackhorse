package com.thoughtworks.blackhorse.config

import com.thoughtworks.blackhorse.printer.PrinterOption
import com.thoughtworks.blackhorse.utils.FileExtension
import java.nio.file.Path

data class StoryContext(
    val storyName: String,
    private val tempPath: Path,
    private val projectContext: ProjectContext,
) {
    val projectName = projectContext.projectName
    val printerOption = projectContext.printerOption
    val printer = projectContext.printer
    val distPath = projectContext.distPath
    val jiraBasUrl: String by lazy { projectContext.jiraBasUrl }
    val jiraToken: String by lazy { projectContext.jiraToken }

    fun isVisible(hiddenOption: HiddenOption) = projectContext.isVisible(hiddenOption)
    fun getProjectFile(filename: String) = FileExtension.getOrCreateFile(filename, distPath)
    fun getTempFile(filename: String) = FileExtension.getOrCreateFile(filename, tempPath)

    fun override(printer: PrinterOption?) = copy(projectContext = projectContext.override(printer))

    companion object {
        fun load(projectName: String, storyName: String) = ProjectContext.load(projectName).loadStory(storyName)
    }
}

fun ProjectContext.loadStory(storyName: String) = StoryContext(
    storyName,
    distPath.resolve("temp").resolve(storyName.lowercase()),
    this
)
