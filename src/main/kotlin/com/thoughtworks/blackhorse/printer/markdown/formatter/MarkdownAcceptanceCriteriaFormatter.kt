package com.thoughtworks.blackhorse.printer.markdown.formatter

import com.thoughtworks.blackhorse.printer.interfaces.AcceptanceCriteriaFormatter
import com.thoughtworks.blackhorse.printer.interfaces.FlowFormatter
import com.thoughtworks.blackhorse.schema.story.AcceptanceCriteria

class MarkdownAcceptanceCriteriaFormatter(
    private val flowFormatter: FlowFormatter,
) : AcceptanceCriteriaFormatter {
    override fun anchors(items: List<AcceptanceCriteria>) =
        items.mapToLines {
            lineOf(
                toAnchorLink(anchor(it.id)),
                flowFormatter.anchors(it.flows).prependIndent("  ")
            )
        }

    override fun acceptanceCriteria(items: List<AcceptanceCriteria>): String =
        items.mapToLines(this::acceptanceCriteria)

    override fun summary(items: List<AcceptanceCriteria>) =
        items.mapToLines { item ->
            lineOf(
                title(item.id),
                item.description,
                example(item.example),
                note(item.note),
            )
        }

    private fun acceptanceCriteria(item: AcceptanceCriteria) = item.run {
        lineOf(
            title(id),
            description,
            example(example),
            mockup(mockup),
            link(link),
            flowFormatter.flows(flows)
        )
    }

    private fun title(id: String) = "### ${anchor(id)}"
    private fun anchor(id: String) = "AC $id"
    private fun example(str: String?) = str?.let {
        lineOf(
            "#### Example",
            it,
        )
    }

    private fun mockup(items: List<String>): String? {
        if (items.isEmpty()) return null

        return lineOf(
            "#### Mockup",
            "----",
            items.mapToLines { "![ac1]($it)" }
        )
    }

    private fun link(items: Map<String, String>): String? {
        if (items.isEmpty()) return null

        return lineOf(
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
