package com.thoughtworks.blackhorse.printer.markdown.formatter

import com.thoughtworks.blackhorse.config.HiddenOption
import com.thoughtworks.blackhorse.config.ProjectConfig
import com.thoughtworks.blackhorse.printer.interfaces.FlowFormatter
import com.thoughtworks.blackhorse.printer.interfaces.ProcessFormatter
import com.thoughtworks.blackhorse.schema.story.Complexity
import com.thoughtworks.blackhorse.schema.story.Flow
import com.thoughtworks.blackhorse.schema.story.FlowProcess

open class MarkdownFlowFormatter(
    private val processFormatter: ProcessFormatter,
) : FlowFormatter {
    override fun anchors(items: List<Flow>): String =
        items.mapIndexedToLines { index, item -> toAnchorLink(anchor(index + 1, item.purpose)) }

    override fun flows(items: List<Flow>) =
        items.mapIndexedToLines { index, item -> flow(index, item) }

    private fun flow(index: Int, item: Flow) = item.run {
        lineOf(
            purpose(index + 1, purpose),
            complexity(complexity),
            content(processes)
        )
    }

    private fun content(processes: List<FlowProcess>): String? {
        if (processes.isEmpty()) return null

        return lineOf(
            "##### Processes",
            processDescription(processes),
            "##### Sequence Diagram",
            processDiagram(processes),
        )
    }

    private fun purpose(serial: Int, str: String) = "#### ${anchor(serial, str)}"
    private fun anchor(serial: Int, str: String) = "Flow $serial $str"
    private fun complexity(complexity: Complexity): String? {
        if (ProjectConfig.isVisible(HiddenOption.COMPLEXITY)) return null

        return when (complexity) {
            Complexity.OUT_OF_SCOPE -> "Out of Scope"
            else -> complexity.run { "- **Complexity**: $name - about **$minutes** minutes" }
        }
    }

    private fun processDescription(processes: List<FlowProcess>) =
        processes.mapToLines(processFormatter::process)

    open fun processDiagram(processes: List<FlowProcess>) = lineOf(
        "```sequence",
        processes.mapToLines(processFormatter::diagram),
        "```",
    )
}
