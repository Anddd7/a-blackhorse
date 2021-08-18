package com.thoughtworks.blackhorse.printer.markdown.formatter

import com.thoughtworks.blackhorse.config.HiddenOption
import com.thoughtworks.blackhorse.config.StoryContextHolder
import com.thoughtworks.blackhorse.printer.interfaces.TaskFormatter
import com.thoughtworks.blackhorse.schema.architecture.ProcessDef
import com.thoughtworks.blackhorse.schema.architecture.attributes.TestDouble
import com.thoughtworks.blackhorse.schema.story.Task
import org.slf4j.LoggerFactory

open class MarkdownTaskFormatter : TaskFormatter {
    private val log = LoggerFactory.getLogger(this.javaClass)

    override fun task(task: Task): String {
        val title = task.title()
        val testDouble = task.dependency()
        val input = task.accept ?: task.targetApiScenario?.apiDefinition?.let { "\\> $it" }
        val output = task.reply ?: task.targetApiScenario?.statusDescription?.let { "< $it" }

        return lineOf(
            "- **$title**",
            lineOf(
                input,
                testDouble.takeIf { !input.isNullOrBlank() || !output.isNullOrBlank() },
                output,
            ).prependIndent("  "),
            "----",
            task.nested.mapToLines(this::task)
        )
    }

    private fun ProcessDef.title(): String {
        val prefix = "Process $name"
        val relations =
            if (component == dependency) component.name()
            else "${component.name()}, depends on $testDouble<${dependency.name()}>"
        val complexity = complexity.label().takeIf { StoryContextHolder.isVisible(HiddenOption.COMPLEXITY) }

        return listOfNotNull(prefix, relations, complexity).joinToString(" | ")
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
        null -> "->"
        else -> "-->"
    }

    open fun Task.inputNote(): String {
        val note = lineOf(
            label(),
            // accept ?: reply.takeIf { start == target },
            // targetApiScenario?.let { lineOf(it.apiDefinition, it.request) }
        ).ifEmpty { return "" }
        return "Note right of $startName:" + note.replace("\n", "\\n")
    }

    open fun Task.outputNote(): String {
        val note = lineOf(
            label(),
            // reply,
            // targetApiScenario?.response
        ).ifEmpty { return "" }
        return "Note left of $targetName:" + note.replace("\n", "\\n")
    }

    private fun Task.go(): String {
        return lineOf(
            startName + " " + lineTo() + " " + targetName + ":", // + input(),
            inputNote()
        )
    }

    private fun Task.back(): String? {
        if (start == target) return null

        return lineOf(
            targetName + " " + lineBack() + " " + startName + ":", // + output(),
            outputNote()
        )
    }
}
