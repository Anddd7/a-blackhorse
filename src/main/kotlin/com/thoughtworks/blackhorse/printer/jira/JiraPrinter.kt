package com.thoughtworks.blackhorse.printer.jira

import com.thoughtworks.blackhorse.config.ProjectConfig
import com.thoughtworks.blackhorse.config.StoryConfig
import com.thoughtworks.blackhorse.printer.interfaces.StoryFormatter
import com.thoughtworks.blackhorse.printer.jira.api.updateAttachments
import com.thoughtworks.blackhorse.printer.jira.api.updateDescription
import com.thoughtworks.blackhorse.printer.pdf.PandocPdfPrinter
import com.thoughtworks.blackhorse.printer.pdf.executePandoc
import com.thoughtworks.blackhorse.schema.story.AcceptanceCriteria
import com.thoughtworks.blackhorse.schema.story.Story
import java.nio.file.Files
import java.nio.file.Path

class JiraPrinter(
    private val formatter: StoryFormatter,
) : PandocPdfPrinter(formatter) {
    override fun story(story: Story) {
        val cardId = story.cardId
        val pdf = outputDocumentationPdf(StoryConfig.storyName(), story)
        val jira = outputJiraDescription(StoryConfig.storyName(), story)
        val mockups = story.acceptanceCriteria
            .flatMap(AcceptanceCriteria::mockup)
            .map { ProjectConfig.distDir().resolve(it) }

        logJiraWelcome()

        updateAttachments(cardId, mockups + pdf)
        updateDescription(cardId, description = formatJiraDescription(jira))
    }

    private fun formatJiraDescription(jira: Path): String {
        val imgRegex = "!([^|]*)\\|alt=img!".toRegex()

        return Files.readAllLines(jira)
            .asSequence()
            .map { it.replace("\\(", "(") }
            .map { it.replace("\\[", "[") }
            .map {
                it.replace(imgRegex) { match ->
                    "!${match.groupValues[1]}|width=600!"
                }
            }
            .joinToString("\\r\\n")
    }

    override fun preview(story: Story) {
        outputDocumentationPdf(StoryConfig.storyName(), story)
        outputJiraDescription(StoryConfig.storyName(), story)
    }

    private fun logJiraWelcome() {
        println(
            """
            *******************************************************
            Start Uploading to Jira: ${ProjectConfig.jiraBaseUrl()}
            *******************************************************
            """.trimIndent()
        )
    }

    private fun outputDocumentationPdf(storyName: String, story: Story): Path {
        val markdown = createMarkdown(storyName)
        val pdf = createPdf(markdown)
        val content = formatter.story(story)
        writeToMarkdown(markdown, content)
        convertToPdf(markdown, pdf)
        return pdf
    }

    private fun outputJiraDescription(storyName: String, story: Story): Path {
        val markdown = createMarkdown(storyName + "_SUMMARY")
        val jira = createTxt(markdown)
        val content = formatter.summary(story)
        writeToMarkdown(markdown, content)
        convertToJira(markdown, jira)
        return jira
    }

    private fun createTxt(markdown: Path) = createAnotherFile(markdown, ".txt")
    private fun convertToJira(markdown: Path, jira: Path) = executePandoc(markdown, jira, "jira")
}
