package com.thoughtworks.blackhorse.printer.markdown.formatter

import com.thoughtworks.blackhorse.printer.interfaces.FlowFormatter
import com.thoughtworks.blackhorse.printer.interfaces.TaskFormatter
import com.thoughtworks.blackhorse.schema.story.Flow
import com.thoughtworks.blackhorse.schema.story.Task

open class MarkdownFlowFormatter(
    private val taskFormatter: TaskFormatter,
) : FlowFormatter {
    override fun anchors(items: List<Flow>) =
        items.mapToLinesWith {
            toAnchorLink(
                anchor(id),
                name(id, example)
            )
        }

    override fun flows(items: List<Flow>) =
        items.mapToLinesWith {
            lineOf(
                title(id, example),
                // example,
                content(tasks)
            )
        }

    private fun content(processes: List<Task>) = when {
        processes.isEmpty() -> null
        else -> lineOf(
            "##### Tasks",
            taskDescription(processes),
            "##### Diagram",
            taskDiagram(processes),
        )
    }

    private fun name(id: String, str: String) = "Flow $id $str"
    private fun anchor(id: String) = "flow-$id"
    private fun title(id: String, str: String) =
        "#### <span id='${anchor(id)}'>${name(id, str)}</span>"

    private fun taskDescription(processes: List<Task>) =
        processes.mapToLines(taskFormatter::task)

    open fun taskDiagram(tasks: List<Task>) = lineOf(
        "```sequence",
        tasks.mapToLines(taskFormatter::diagram),
        "```",
    )
}
