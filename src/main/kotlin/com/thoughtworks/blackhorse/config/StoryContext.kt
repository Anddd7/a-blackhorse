package com.thoughtworks.blackhorse.config

import com.thoughtworks.blackhorse.printer.PrinterOption
import java.nio.file.Path

data class StoryContext(
    val projectContext: ProjectContext,
    val storyName: String,
) {
    val tempPath: Path = projectContext.distPath
        .resolve("temp")
        .resolve(storyName.lowercase())

    fun override(printer: PrinterOption?) = copy(
        projectContext = projectContext.override(printer)
    )

    companion object {
        fun load(projectName: String, storyName: String) = ProjectContext.load(projectName).loadStory(storyName)
    }
}

fun ProjectContext.loadStory(storyName: String) = StoryContext(this, storyName)
