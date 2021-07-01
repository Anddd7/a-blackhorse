package com.thoughtworks.blackhorse.printer.markdown

import com.thoughtworks.blackhorse.config.StoryContextHolder
import com.thoughtworks.blackhorse.printer.interfaces.StoryFormatter
import com.thoughtworks.blackhorse.printer.interfaces.StoryPrinter
import com.thoughtworks.blackhorse.schema.story.Story
import com.thoughtworks.blackhorse.utils.FileExtension
import java.nio.file.Path

open class MarkdownPrinter(
    private val formatter: StoryFormatter,
) : StoryPrinter {
    override fun start(story: Story) {
        outputToDocumentationMarkdown(story)
    }

    fun outputToDocumentationMarkdown(story: Story): Path {
        val content = formatter.story(story)
        val file = StoryContextHolder.getProjectFile("${story.name}.md")
        FileExtension.writeToMarkdown(file, content)

        return file
    }
}
