package com.thoughtworks.blackhorse.printer.markdown

import com.thoughtworks.blackhorse.config.StoryContextHolder
import com.thoughtworks.blackhorse.printer.interfaces.StoryFormatter
import com.thoughtworks.blackhorse.printer.interfaces.StoryPrinter
import com.thoughtworks.blackhorse.schema.story.Story
import com.thoughtworks.blackhorse.utils.FileExtension

open class MarkdownPrinter(
    private val formatter: StoryFormatter,
) : StoryPrinter {
    override fun start(story: Story) {
        val context = StoryContextHolder.get()

        val file = context.getProjectFile("${story.name}.md")
        val content = formatter.story(story)
        FileExtension.writeToMarkdown(file, content)
    }
}
