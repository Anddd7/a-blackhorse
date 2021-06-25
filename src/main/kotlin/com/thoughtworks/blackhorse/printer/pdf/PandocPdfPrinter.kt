package com.thoughtworks.blackhorse.printer.pdf

import com.thoughtworks.blackhorse.config.ProjectConfig
import com.thoughtworks.blackhorse.config.StoryConfig
import com.thoughtworks.blackhorse.config.getOrCreateFile
import com.thoughtworks.blackhorse.printer.interfaces.StoryFormatter
import com.thoughtworks.blackhorse.printer.markdown.MarkdownPrinter
import com.thoughtworks.blackhorse.schema.story.Story
import com.thoughtworks.blackhorse.utils.ShellCli
import com.thoughtworks.blackhorse.utils.ShellOperation
import java.nio.file.Path
import kotlin.io.path.nameWithoutExtension

open class PandocPdfPrinter(
    private val formatter: StoryFormatter,
) : MarkdownPrinter(formatter) {
    override fun story(story: Story) {
        val markdown = createMarkdown(StoryConfig.storyName())
        val pdf = createPdf(markdown)
        val content = formatter.story(story)
        writeToMarkdown(markdown, content)
        convertToPdf(markdown, pdf)
    }

    fun createPdf(ref: Path) = createAnotherFile(ref, ".pdf")
    fun createAnotherFile(ref: Path, ext: String): Path {
        val filename = ref.fileName.nameWithoutExtension + ext
        return getOrCreateFile(filename, ref.parent)
    }

    fun convertToPdf(source: Path, target: Path) = executePandoc(source, target, "pdf")
}

fun executePandoc(source: Path, target: Path, format: String) {
    val projectFolder = ProjectConfig.distDir()
    val sourceFile = source.fileName
    val targetFile = target.fileName
    ShellOperation.execute(ShellCli.PANDOC, projectFolder, sourceFile, format, targetFile)
}
