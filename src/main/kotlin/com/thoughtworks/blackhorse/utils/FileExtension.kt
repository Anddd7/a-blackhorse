package com.thoughtworks.blackhorse.utils

import com.thoughtworks.blackhorse.config.StoryContextHolder
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.nio.file.Files
import java.nio.file.Path
import kotlin.io.path.nameWithoutExtension

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

    fun convertToPdf(markdown: Path, target: Path) = executePandoc(markdown, target, "pdf")
    fun convertToJira(markdown: Path, jira: Path) = executePandoc(markdown, jira, "jira")
    private fun executePandoc(source: Path, target: Path, format: String) {
        val projectFolder = StoryContextHolder.distPath()
        val sourceFile = source.fileName
        val targetFile = target.fileName
        ShellOperation.execute(ShellCli.PANDOC, projectFolder, sourceFile, format, targetFile)
    }

    fun createMirrorPdf(source: Path) = createMirrorFile(source, ".pdf")
    fun createMirrorTxt(source: Path) = createMirrorFile(source, ".txt")
    private fun createMirrorFile(source: Path, ext: String): Path {
        val folder = source.parent
        val filename = source.fileName.nameWithoutExtension + ext
        return getOrCreateFile(filename, folder)
    }
}
