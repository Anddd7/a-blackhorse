package com.thoughtworks.blackhorse.printer.markdown.formatter

import com.thoughtworks.blackhorse.printer.interfaces.AcceptanceCriteriaFormatter
import com.thoughtworks.blackhorse.printer.interfaces.FlowFormatter
import com.thoughtworks.blackhorse.schema.story.AcceptanceCriteria

class MarkdownAcceptanceCriteriaFormatter(
    private val flowFormatter: FlowFormatter,
) : AcceptanceCriteriaFormatter {
    override fun anchors(items: List<AcceptanceCriteria>) =
        items.mapToLines {
            it.run {
                lineOf(
                    toAnchorLink(label(id)),
                    flowFormatter.anchors(flows).prependIndent("  ")
                )
            }
        }

    override fun acceptanceCriteria(items: List<AcceptanceCriteria>) =
        items.mapToLines {
            it.run {
                lineOf(
                    title(id),
                    description,
                    example(example),
                    mockup(mockup),
                    link(link),
                    flowFormatter.flows(flows)
                )
            }
        }

    override fun summary(items: List<AcceptanceCriteria>) =
        items.mapToLines { item ->
            lineOf(
                title(item.id),
                item.description,
                example(item.example),
                note(item.note),
            )
        }

    private fun title(id: String) = "### ${label(id)}"
    private fun label(id: String) = "AC $id"
    private fun example(str: String?) = str?.let {
        lineOf(
            "#### Example",
            it,
        )
    }

    private fun mockup(items: List<String>) = when {
        items.isEmpty() -> null
        else -> lineOf(
            "#### Mockup",
            "----",
            items.mapToLines { "![ac1]($it)" }
        )
    }

    private fun link(items: Map<String, String>) = when {
        items.isEmpty() -> null
        else -> lineOf(
            "#### Links",
            items.entries.mapToLines { "- [${it.key}](${it.value})" }
        )
    }

    private fun note(str: String?) = str?.let {
        lineOf(
            "#### Notes",
            it,
        )
    }
}
