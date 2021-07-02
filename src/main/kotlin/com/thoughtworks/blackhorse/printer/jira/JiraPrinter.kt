package com.thoughtworks.blackhorse.printer.jira

import com.thoughtworks.blackhorse.config.StoryContextHolder
import com.thoughtworks.blackhorse.printer.interfaces.StoryFormatter
import com.thoughtworks.blackhorse.printer.jira.api.updateAttachments
import com.thoughtworks.blackhorse.printer.jira.api.updateCardInformation
import com.thoughtworks.blackhorse.printer.markdown.formatter.lineOf
import com.thoughtworks.blackhorse.printer.pdf.PandocPdfPrinter
import com.thoughtworks.blackhorse.schema.story.AcceptanceCriteria
import com.thoughtworks.blackhorse.schema.story.Story
import com.thoughtworks.blackhorse.utils.FileExtension
import org.slf4j.LoggerFactory
import java.nio.file.Files
import java.nio.file.Path

class JiraPrinter(
    private val formatter: StoryFormatter,
) : PandocPdfPrinter(formatter) {
    private val log = LoggerFactory.getLogger(this.javaClass)

    override fun start(story: Story) {
        val pdf = outputToDocumentationPdf(story)
        val jira = outputJiraDescription(story)
        val mockups = story.acceptanceCriteria
            .flatMap(AcceptanceCriteria::mockup)
            .map { StoryContextHolder.distPath().resolve(it) }

        log.info("-------------------------------------------------------")
        log.info("Start Uploading to Jira: ${StoryContextHolder.jiraBasUrl()}")

        updateAttachments(story.cardId, mockups + listOf(pdf))
        updateCardInformation(story.cardId, story.title, story.estimation, formatJiraDescription(jira))

        log.info("-------------------------------------------------------")
    }

    private fun outputToDocumentationPdf(story: Story): Path {
        val content = formatter.story(story)

        val markdown = StoryContextHolder.getLocalStoryFile()
        FileExtension.writeToMarkdown(markdown, content)

        val pdf = FileExtension.createMirrorPdf(markdown)
        FileExtension.convertToPdf(markdown, pdf)

        return pdf
    }

    private fun outputJiraDescription(story: Story): Path {
        val prefix = "Markdown Source File: [Click Here](${StoryContextHolder.getRemoteStoryUrl()})"
        val content = lineOf(
            prefix,
            formatter.summary(story)
        )

        val markdown = StoryContextHolder.getLocalSummaryFile()
        FileExtension.writeToMarkdown(markdown, content)

        val jira = FileExtension.createMirrorTxt(markdown)
        FileExtension.convertToJira(markdown, jira)

        return jira
    }

    companion object {
        private fun formatJiraDescription(jira: Path): String {
            val imgRegex = "!([^|]*)\\|alt=img!".toRegex()

            return Files.readAllLines(jira)
                .asSequence()
                .map { it.replace("\\(", "(") }
                .map { it.replace("\\[", "[") }
                .map { it.replace("\"", "\\\"") }
                .map {
                    it.replace(imgRegex) { match ->
                        "!${match.groupValues[1]}|width=600!"
                    }
                }
                .joinToString("\\r\\n")
        }
    }
}
