package com.thoughtworks.blackhorse.config

import com.thoughtworks.blackhorse.utils.FileExtension
import kotlinx.coroutines.asContextElement
import java.nio.file.Path

object StoryContextHolder {
    private val instance = ThreadLocal<StoryContext>()

    fun set(new: StoryContext) = instance.set(new)
    fun get() = instance.get() ?: throw IllegalAccessException("run StoryContext.load() first")

    // expose quick methods
    private fun repoBaseUrl() = get().projectContext.repoBaseUrl
    private fun storyName() = get().storyName
    private fun tempPath() = get().tempPath
    fun projectName() = get().projectContext.projectName
    fun storyPrinter() = get().projectContext.storyPrinter
    fun distPath(): Path = get().projectContext.distPath
    fun jiraBasUrl() = get().projectContext.jiraBasUrl
    fun isVisible(hiddenOption: HiddenOption) = get().projectContext.isVisible(hiddenOption)

    fun getLocalStoryFile(): Path = FileExtension.getOrCreateFile(storyName() + ".md", distPath())
    fun getLocalSummaryFile(): Path = FileExtension.getOrCreateFile(storyName() + "_SUMMARY.md", distPath())
    fun getLocalStoryTempFile(filename: String): Path = FileExtension.getOrCreateFile(filename, tempPath())

    fun getRemoteStoryUrl(): Path = repoBaseUrl().resolve(distPath()).resolve(storyName() + ".md")

    // coroutine
    fun asContextElement() = instance.asContextElement()
}
