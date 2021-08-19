package com.thoughtworks.blackhorse.printer.markdown.formatter

import com.thoughtworks.blackhorse.printer.interfaces.AcceptanceCriteriaFormatter
import com.thoughtworks.blackhorse.printer.interfaces.FlowFormatter
import com.thoughtworks.blackhorse.schema.story.AcceptanceCriteria

class MarkdownAcceptanceCriteriaFormatter(
    private val flowFormatter: FlowFormatter,
) : AcceptanceCriteriaFormatter {
    override fun anchors(items: List<AcceptanceCriteria>) =
        items.mapToLinesWith {
            lineOf(
                toAnchorLink(anchor(id), name(id, description)),
                flowFormatter.anchors(flows).prependIndent("  ")
            )
        }

    override fun acceptanceCriteria(items: List<AcceptanceCriteria>) =
        items.mapToLinesWith {
            lineOf(
                title(id, null),
                description,
                mockup(mockup),
                link(link),
                flowFormatter.flows(flows)
            )
        }

    override fun summary(items: List<AcceptanceCriteria>) =
        items.mapToLinesWith {
            lineOf(
                title(id, description),
                description,
                note(note),
                mockup(mockup, ignorePath = true),
                link(link),
            )
        }

    private fun name(id: String, str: String) = "AC $id $str"
    private fun anchor(id: String) = "ac-$id"
    private fun title(id: String, str: String?): String {
        val label = str?.let {
            if (it.length > 55) it.substring(0, 55) + "..."
            else it
        } ?: ""
        return "### <span id='${anchor(id)}'>${name(id, label)}</span>"
    }

    private fun mockup(items: List<String>, ignorePath: Boolean = false) = when {
        items.isEmpty() -> null
        else -> lineOf(
            "#### Mockup",
            "----",
            items
                .map { if (ignorePath) it.substringAfter("/") else it }
                .mapToLines { "![img]($it)" }
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
