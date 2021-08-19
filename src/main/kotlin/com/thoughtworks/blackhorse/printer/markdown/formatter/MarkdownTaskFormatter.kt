package com.thoughtworks.blackhorse.printer.markdown.formatter

import com.thoughtworks.blackhorse.config.HiddenOption
import com.thoughtworks.blackhorse.config.StoryContextHolder
import com.thoughtworks.blackhorse.printer.interfaces.TaskFormatter
import com.thoughtworks.blackhorse.schema.architecture.Ext
import com.thoughtworks.blackhorse.schema.architecture.ProcessDef
import com.thoughtworks.blackhorse.schema.architecture.attributes.TestDouble
import com.thoughtworks.blackhorse.schema.story.Task
import org.slf4j.LoggerFactory

open class MarkdownTaskFormatter : TaskFormatter {
    private val log = LoggerFactory.getLogger(this.javaClass)

    override fun task(task: Task): String =
        lineOf(
            task.title(),
            lineOf(
                task.accept,
                task.reply,
                task.targetApiScenario?.apiDefinition?.let { "\\> $it" },
                task.targetApiScenario?.statusDescription?.let { "< $it" },
            ).prependIndent("  "),
            " ",
            "----",
            task.nested.mapToLines(this::task)
        )

    private fun ProcessDef.title(): String {
        val prefix = "Process $name"
        val relations =
            if (component == dependency) component.name()
            else "${component.name()}, depends on $testDouble<${dependency.name()}>" + (testFramework?.let { ", using $it" })
        val complexity = complexity.label().takeIf { StoryContextHolder.isVisible(HiddenOption.COMPLEXITY) }

        return lineOf(
            "- **$prefix | $complexity**",
            "> $relations",
            " "
        )
    }

    private fun Task.title(): String {
        if (process == null) {
            log.warn("!![WARNING] No process definition between [$startName] and [$targetName].")
            return "Inner Logic | $startName"
        }

        return process.title()
    }

    private fun Task.label() =
        process?.run { "Process $name" } ?: "Inner Logic"

    private fun Task.dependency() =
        process?.run {
            "*${component.name()} -> $testDouble<${dependency.name()}>*".takeIf { testDouble != TestDouble.Real }
        }

    override fun diagram(task: Task): String = lineOf(
        task.go(),
        task.nested.mapToLines(this::diagram),
        task.back()
    )

    open fun Task.lineTo() = when (process?.testDouble) {
        null -> "->"
        else -> "-->"
    }

    open fun Task.lineBack() = when (process?.testDouble) {
        null -> "<-"
        else -> "<--"
    }

    open fun Task.inputNote(): String {
        val (direction, node) = when (start) {
            Ext.Unknown -> "left" to targetName
            else -> "right" to startName
        }

        val note = lineOf(
            label(),
            // accept ?: reply.takeIf { start == target },
            // targetApiScenario?.let { lineOf(it.apiDefinition, it.request) }
        ).ifEmpty { return "" }
        return "Note $direction of $node:" + note.replace("\n", "\\n")
    }

    open fun Task.outputNote(): String {
        val (direction, node) = when (target) {
            Ext.Unknown -> "right" to startName
            else -> "left" to targetName
        }

        val note = lineOf(
            label(),
            // reply,
            // targetApiScenario?.response
        ).ifEmpty { return "" }
        return "Note $direction of $node:" + note.replace("\n", "\\n")
    }

    private fun Task.go(): String {
        val part1 = if (start == Ext.Unknown) "[" else "$startName "
        val part2 = if (target == Ext.Unknown) "]" else " $targetName"

        return lineOf(
            part1 + lineTo() + part2 + ":", // + input(),
            inputNote()
        )
    }

    private fun Task.back(): String? {
        if (start == target) return null

        val part1 = if (start == Ext.Unknown) "[" else "$startName "
        val part2 = if (target == Ext.Unknown) "]" else " $targetName"

        return lineOf(
            part1 + lineBack() + part2 + ":", // + output(),
            outputNote()
        )
    }
}
