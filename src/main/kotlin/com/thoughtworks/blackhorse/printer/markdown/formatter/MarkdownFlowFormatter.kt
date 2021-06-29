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
        items.mapToLines { item -> toAnchorLink(anchor(item.id, item.purpose)) }

    override fun flows(items: List<Flow>) =
        items.mapToLines { item -> flow(item) }

    private fun flow(item: Flow) = item.run {
        lineOf(
            purpose(id, purpose),
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

    private fun purpose(id: String, str: String) = "#### ${anchor(id, str)}"
    private fun anchor(id: String, str: String) = "Flow $id $str"
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
