package com.thoughtworks.blackhorse.printer.markdown.formatter

import com.thoughtworks.blackhorse.printer.interfaces.AcceptanceCriteriaFormatter
import com.thoughtworks.blackhorse.printer.interfaces.FlowFormatter
import com.thoughtworks.blackhorse.schema.story.AcceptanceCriteria

class MarkdownAcceptanceCriteriaFormatter(
    private val flowFormatter: FlowFormatter,
) : AcceptanceCriteriaFormatter {
    override fun anchors(items: List<AcceptanceCriteria>) =
        items.mapIndexedToLines { index, item ->
            val serial = index + 1

            lineOf(
                toAnchorLink(anchor(serial)),
                flowFormatter.anchors(item.flows, serial).prependIndent("  ")
            )
        }

    override fun acceptanceCriteria(items: List<AcceptanceCriteria>) =
        items.mapIndexedToLines { index, item -> acceptanceCriteria(index + 1, item) }

    override fun summary(items: List<AcceptanceCriteria>) =
        items.mapIndexedToLines { index, item ->
            lineOf(
                title(index + 1),
                item.description,
                example(item.example),
                note(item.note),
            )
        }

    private fun acceptanceCriteria(serial: Int, item: AcceptanceCriteria) = item.run {
        lineOf(
            title(serial),
            description,
            example(example),
            mockup(mockup),
            link(link),
            flowFormatter.flows(flows, serial)
        )
    }

    private fun title(serial: Int) = "### ${anchor(serial)}"
    private fun anchor(serial: Int) = "AC $serial"
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
