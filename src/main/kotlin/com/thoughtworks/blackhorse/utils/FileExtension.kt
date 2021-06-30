package com.thoughtworks.blackhorse.utils

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.nio.file.Files
import java.nio.file.Path

object FileExtension {
    private val log: Logger = LoggerFactory.getLogger(this.javaClass)

    fun getOrCreateFile(filename: String, folder: Path): Path {
        if (Files.notExists(folder)) Files.createDirectories(folder)
        val file = folder.resolve(filename)
        if (Files.notExists(file)) Files.createFile(file)
        return file
    }

    fun writeToMarkdown(file: Path, content: String) {
        runCatching { Files.writeString(file, content) }
            .onSuccess { log.info("Generated successfully, open with markdown reader (recommend Typora): ${it.toAbsolutePath()}") }
            .onFailure { log.error("Cannot generate ${file.fileName} as a markdown file.") }
    }
}
