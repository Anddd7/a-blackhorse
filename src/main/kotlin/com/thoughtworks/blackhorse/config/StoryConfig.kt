package com.thoughtworks.blackhorse.config

import java.nio.file.Files
import java.nio.file.Path

data class StoryConfig(
    val storyName: String,
    val tempPath: Path,
) {
    fun clearUp() {
        if (Files.exists(tempPath))
            Files.walk(tempPath)
                .filter { !it.toFile().isDirectory }
                .forEach { Files.deleteIfExists(it) }
    }

    companion object {
        private val current = ThreadLocal<StoryConfig>()

        fun load(storyName: String) {
            val tempPath = ProjectConfig.distDir().resolve("temp").resolve(storyName.lowercase())
            val storyConfig = StoryConfig(storyName, tempPath)
            storyConfig.clearUp()

            current.set(storyConfig)
        }

        private fun instance() = current.get() ?: throw IllegalAccessException("run StoryConfig.load() first")

        // quick methods
        private fun tempPath() = instance().tempPath
        fun getTempFile(filename: String) = getOrCreateFile(filename, tempPath())
        fun storyName() = instance().storyName

        fun execute(storyName: String, fn: () -> Unit) {
            println("--> Print story #$storyName")
            val startTime = System.currentTimeMillis()
            load(storyName)

            fn()

            println("--> Print story #$storyName finished in ${System.currentTimeMillis() - startTime} ms")
        }
    }
}
