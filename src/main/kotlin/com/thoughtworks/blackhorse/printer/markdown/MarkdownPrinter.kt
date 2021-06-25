package com.thoughtworks.blackhorse.printer.markdown

import com.thoughtworks.blackhorse.config.ProjectConfig.Companion.getProjectFile
import com.thoughtworks.blackhorse.config.StoryConfig
import com.thoughtworks.blackhorse.printer.interfaces.StoryFormatter
import com.thoughtworks.blackhorse.printer.interfaces.StoryPrinter
import com.thoughtworks.blackhorse.schema.story.Story
import java.nio.file.Files
import java.nio.file.Path

open class MarkdownPrinter(
    private val formatter: StoryFormatter,
) : StoryPrinter {
    override fun story(story: Story) {
        val file = createMarkdown(StoryConfig.storyName())
        val content = formatter.story(story)
        writeToMarkdown(file, content)
    }

    companion object {
        fun writeToMarkdown(file: Path, content: String) {
            runCatching { Files.writeString(file, content) }
                .onSuccess { println("Generated successfully, open with markdown reader (recommend Typora): ${it.toAbsolutePath()}") }
                .onFailure { println("Cannot generate ${file.fileName} as a markdown file.") }
        }

        fun createMarkdown(storyName: String) = getProjectFile("$storyName.md")
    }
}
