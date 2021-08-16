package com.thoughtworks.blackhorse.printer.pdf

import com.thoughtworks.blackhorse.config.StoryContextHolder
import com.thoughtworks.blackhorse.printer.interfaces.StoryFormatter
import com.thoughtworks.blackhorse.printer.markdown.MarkdownStoryPrinter
import com.thoughtworks.blackhorse.schema.story.Story
import com.thoughtworks.blackhorse.utils.FileExtension

open class PandocPdfStoryPrinter(
    private val formatter: StoryFormatter
) : MarkdownStoryPrinter(formatter) {
    override fun start(story: Story) {
        val content = formatter.story(story)

        val markdown = StoryContextHolder.getLocalStoryFile()
        FileExtension.writeToMarkdown(markdown, content)

        val pdf = FileExtension.createMirrorPdf(markdown)
        FileExtension.convertToPdf(markdown, pdf)
    }
}
