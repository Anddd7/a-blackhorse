package com.thoughtworks.blackhorse.config

import com.thoughtworks.blackhorse.utils.FileExtension
import kotlinx.coroutines.asContextElement
import java.nio.file.Path

object ProjectContextHolder {
    private val instance = ThreadLocal<ProjectContext>()

    fun set(new: ProjectContext) = instance.set(new)
    fun get() =
        try {
            instance.get() ?: StoryContextHolder.get().projectContext
        } catch (e: IllegalAccessException) {
            throw IllegalAccessException("run ProjectContext.load() first")
        }

    // expose quick methods
    private fun repoBaseUrl() = get().repoBaseUrl
    fun projectName() = get().projectName
    fun architecturePrinter() = get().architecturePrinter
    fun distPath(): Path = get().distPath

    fun getLocalStoryTempFile(filename: String): Path =
        FileExtension.getOrCreateFile(filename, distPath().resolve("temp"))

    fun createProjectFileIfAbsent(filename: String) =
        FileExtension.createFileIfAbsent(filename, distPath())

    // coroutine
    fun asContextElement() = instance.asContextElement()
}
