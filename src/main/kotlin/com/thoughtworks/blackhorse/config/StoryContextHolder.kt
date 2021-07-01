package com.thoughtworks.blackhorse.config

import com.thoughtworks.blackhorse.utils.FileExtension
import kotlinx.coroutines.asContextElement
import java.nio.file.Path

object StoryContextHolder {
    private val instance = ThreadLocal<StoryContext>()

    fun set(new: StoryContext) = instance.set(new)
    fun get() = instance.get() ?: throw IllegalAccessException("run StoryContext.load() first")

    // expose quick methods
    fun projectName() = get().projectContext.projectName
    fun printer() = get().projectContext.printer
    fun distPath(): Path = get().projectContext.distPath
    fun jiraBasUrl() = get().projectContext.jiraBasUrl
    fun jiraToken() = get().projectContext.jiraToken
    fun isVisible(hiddenOption: HiddenOption) = get().projectContext.isVisible(hiddenOption)
    fun getProjectFile(filename: String) = FileExtension.getOrCreateFile(filename, distPath())
    fun getTempFile(filename: String) = FileExtension.getOrCreateFile(filename, get().tempPath)

    // coroutine
    fun asContextElement() = instance.asContextElement()
}
