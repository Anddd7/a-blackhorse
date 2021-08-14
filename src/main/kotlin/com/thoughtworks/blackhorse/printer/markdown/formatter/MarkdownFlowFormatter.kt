package com.thoughtworks.blackhorse.printer.markdown.formatter

import com.thoughtworks.blackhorse.printer.interfaces.FlowFormatter
import com.thoughtworks.blackhorse.printer.interfaces.ProcessFormatter
import com.thoughtworks.blackhorse.schema.story.Flow
import com.thoughtworks.blackhorse.schema.story.FlowProcess

open class MarkdownFlowFormatter(
    private val processFormatter: ProcessFormatter,
) : FlowFormatter {
    override fun anchors(items: List<Flow>) =
        items.mapToLinesWith {
            toAnchorLink(
                anchor(id),
                name(id)
            )
        }

    override fun flows(items: List<Flow>) =
        items.mapToLinesWith {
            lineOf(
                title(id),
                example,
                content(processes)
            )
        }

    private fun content(processes: List<FlowProcess>) = when {
        processes.isEmpty() -> null
        else -> lineOf(
            "##### Processes",
            processDescription(processes),
            "##### Sequence Diagram",
            processDiagram(processes),
        )
    }

    private fun name(id: String) = "Flow $id"
    private fun anchor(id: String) = "flow-$id"
    private fun title(id: String) = "#### <span id='${anchor(id)}'>${name(id)}</span>"

    private fun processDescription(processes: List<FlowProcess>) =
        processes.mapToLines(processFormatter::process)

    open fun processDiagram(processes: List<FlowProcess>) = lineOf(
        "```sequence",
        processes.mapToLines(processFormatter::diagram),
        "```",
    )
}
