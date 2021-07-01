package com.thoughtworks.blackhorse.printer.pdf

import com.thoughtworks.blackhorse.config.StoryContextHolder
import com.thoughtworks.blackhorse.printer.interfaces.StoryFormatter
import com.thoughtworks.blackhorse.printer.markdown.MarkdownPrinter
import com.thoughtworks.blackhorse.schema.story.Story
import com.thoughtworks.blackhorse.utils.FileExtension
import com.thoughtworks.blackhorse.utils.ShellCli
import com.thoughtworks.blackhorse.utils.ShellOperation
import java.nio.file.Path
import kotlin.io.path.nameWithoutExtension

open class PandocPdfPrinter(
    private val formatter: StoryFormatter,
) : MarkdownPrinter(formatter) {
    override fun start(story: Story) {
        val markdown = StoryContextHolder.getProjectFile("${story.name}.md")
        val pdf = createPdf(markdown)
        val content = formatter.story(story)
        FileExtension.writeToMarkdown(markdown, content)
        convertToPdf(markdown, pdf)
    }

    fun createPdf(ref: Path) = createAnotherFile(ref, ".pdf")
    fun createAnotherFile(ref: Path, ext: String): Path {
        val filename = ref.fileName.nameWithoutExtension + ext
        return FileExtension.getOrCreateFile(filename, ref.parent)
    }

    fun convertToPdf(source: Path, target: Path) = executePandoc(source, target, "pdf")
}

fun executePandoc(source: Path, target: Path, format: String) {
    val projectFolder = StoryContextHolder.distPath()
    val sourceFile = source.fileName
    val targetFile = target.fileName
    ShellOperation.execute(ShellCli.PANDOC, projectFolder, sourceFile, format, targetFile)
}
