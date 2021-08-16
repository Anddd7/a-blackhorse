package com.thoughtworks.blackhorse.printer.markdown.formatter

import com.thoughtworks.blackhorse.printer.interfaces.AcceptanceCriteriaFormatter
import com.thoughtworks.blackhorse.printer.interfaces.ApiSchemaFormatter
import com.thoughtworks.blackhorse.printer.interfaces.ContainerFormatter
import com.thoughtworks.blackhorse.printer.interfaces.StoryFormatter
import com.thoughtworks.blackhorse.schema.story.Story

class MarkdownStoryFormatter(
    private val acFormatter: AcceptanceCriteriaFormatter,
    private val apiFormatter: ApiSchemaFormatter,
) : StoryFormatter {

    override fun story(story: Story) = story.run {
        lineOf(
            anchors(story),
            title(title),
            inScope(inScope),
            outOfScope(outOfScope),
            acFormatter.acceptanceCriteria(acceptanceCriteria),
            apiFormatter.schemas(apis),
        )
    }

    override fun summary(story: Story) = story.run {
        lineOf(
            inScope(inScope),
            outOfScope(outOfScope),
            acFormatter.summary(acceptanceCriteria),
        )
    }

    private fun title(str: String) = "# $str"
    private fun inScope(str: String?) = str?.let {
        lineOf(
            "### In Scope",
            it,
        )
    }

    private fun outOfScope(str: String?) = str?.let {
        lineOf(
            "### Out of Scope",
            it,
        )
    }

    private fun anchors(story: Story) = lineOf(
        "### Table of Content",
        toAnchorLink("In Scope"),
        toAnchorLink("Out of Scope"),
        acFormatter.anchors(story.acceptanceCriteria),
        apiFormatter.anchors(story.apis),
    )
}

fun lineOf(vararg elements: String?) = listOfNotNull(*elements).toLines()
fun List<String>.toLines() = filter(String::isNotEmpty).joinToString("\n")

fun <T> Iterable<T>.mapToLines(transform: (T) -> String) = map(transform).toLines()
fun <T> Iterable<T>.mapIndexedToLines(transform: (index: Int, T) -> String) = mapIndexed(transform).toLines()
fun <T> Iterable<T>.mapToLinesWith(transform: T.() -> String) = map { it.transform() }.toLines()

fun toAnchorLink(id: String, title: String = id): String {
    val link = id
        .replace(" ", "-")
        .lowercase()
    return "- [$title](#$link)"
}
