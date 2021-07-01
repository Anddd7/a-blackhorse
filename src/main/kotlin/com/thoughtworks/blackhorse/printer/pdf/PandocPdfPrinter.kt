package com.thoughtworks.blackhorse.printer.pdf

import com.thoughtworks.blackhorse.printer.interfaces.StoryFormatter
import com.thoughtworks.blackhorse.printer.markdown.MarkdownPrinter
import com.thoughtworks.blackhorse.schema.story.Story
import com.thoughtworks.blackhorse.utils.FileExtension
import java.nio.file.Path

open class PandocPdfPrinter(formatter: StoryFormatter) : MarkdownPrinter(formatter) {
    override fun start(story: Story) {
        outputToDocumentationPdf(story)
    }

    fun outputToDocumentationPdf(story: Story): Path {
        val markdown = outputToDocumentationMarkdown(story)
        val pdf = FileExtension.createMirrorPdf(markdown)
        FileExtension.convertToPdf(markdown, pdf)

        return pdf
    }
}
