package com.thoughtworks.blackhorse.printer.pdf

import com.thoughtworks.blackhorse.config.StoryContextHolder
import com.thoughtworks.blackhorse.printer.interfaces.StoryFormatter
import com.thoughtworks.blackhorse.printer.markdown.MarkdownPrinter
import com.thoughtworks.blackhorse.schema.story.Story
import com.thoughtworks.blackhorse.utils.FileExtension

open class PandocPdfPrinter(
    private val formatter: StoryFormatter
) : MarkdownPrinter(formatter) {
    override fun start(story: Story) {
        val content = formatter.story(story)

        val markdown = StoryContextHolder.getLocalStoryFile()
        FileExtension.writeToMarkdown(markdown, content)

        val pdf = FileExtension.createMirrorPdf(markdown)
        FileExtension.convertToPdf(markdown, pdf)
    }
}
