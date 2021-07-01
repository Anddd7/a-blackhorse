package com.thoughtworks.blackhorse.printer.jira

import com.thoughtworks.blackhorse.config.StoryContextHolder
import com.thoughtworks.blackhorse.printer.interfaces.StoryFormatter
import com.thoughtworks.blackhorse.printer.jira.api.updateAttachments
import com.thoughtworks.blackhorse.printer.jira.api.updateCardInformation
import com.thoughtworks.blackhorse.printer.pdf.PandocPdfPrinter
import com.thoughtworks.blackhorse.printer.pdf.executePandoc
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
        val cardId = story.cardId
        val pdf = outputDocumentationPdf(story.name, story)
        val jira = outputJiraDescription(story.name, story)
        val mockups = story.acceptanceCriteria
            .flatMap(AcceptanceCriteria::mockup)
            .map { StoryContextHolder.distPath().resolve(it) }

        log.info("-------------------------------------------------------")
        log.info("Start Uploading to Jira: ${StoryContextHolder.jiraBasUrl()}")

        updateAttachments(cardId, mockups + listOf(pdf))
        updateCardInformation(cardId, story.title, story.estimation, formatJiraDescription(jira))

        log.info("-------------------------------------------------------")
    }

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

    private fun outputDocumentationPdf(storyName: String, story: Story): Path {
        val markdown = StoryContextHolder.getProjectFile("$storyName.md")
        val pdf = createPdf(markdown)
        val content = formatter.story(story)
        FileExtension.writeToMarkdown(markdown, content)
        convertToPdf(markdown, pdf)
        return pdf
    }

    private fun outputJiraDescription(storyName: String, story: Story): Path {
        val markdown = StoryContextHolder.getProjectFile("${storyName + "_SUMMARY"}.md")
        val jira = createTxt(markdown)
        val content = formatter.summary(story)
        FileExtension.writeToMarkdown(markdown, content)
        convertToJira(markdown, jira)
        return jira
    }

    private fun createTxt(markdown: Path) = createAnotherFile(markdown, ".txt")
    private fun convertToJira(markdown: Path, jira: Path) = executePandoc(markdown, jira, "jira")
}
