package com.thoughtworks.blackhorse.printer.markdown.formatter

import com.thoughtworks.blackhorse.printer.interfaces.FlowFormatter
import com.thoughtworks.blackhorse.printer.interfaces.ProcessFormatter
import com.thoughtworks.blackhorse.schema.story.Flow
import com.thoughtworks.blackhorse.schema.story.FlowProcess

open class MarkdownFlowFormatter(
    private val processFormatter: ProcessFormatter,
) : FlowFormatter {
    override fun anchors(items: List<Flow>): String =
        items.mapToLines { item -> toAnchorLink(anchor(item.id, item.example)) }

    override fun flows(items: List<Flow>) =
        items.mapToLines { item ->
            item.run {
                lineOf(
                    purpose(id, example),
                    example(example),
                    content(processes)
                )
            }
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

    private fun purpose(id: String, str: String) = "#### ${anchor(id, str)}"
    private fun anchor(id: String, str: String) = "Flow $id $str"
    private fun example(str: String) = lineOf(
        "#### Example",
        str,
    )

    private fun processDescription(processes: List<FlowProcess>) =
        processes.mapToLines(processFormatter::process)

    open fun processDiagram(processes: List<FlowProcess>) = lineOf(
        "```sequence",
        processes.mapToLines(processFormatter::diagram),
        "```",
    )
}
