package com.thoughtworks.blackhorse.printer.markdown.formatter

import com.thoughtworks.blackhorse.printer.interfaces.ProcessFormatter
import com.thoughtworks.blackhorse.schema.architecture.ProcessDefinition
import com.thoughtworks.blackhorse.schema.architecture.TestDouble
import com.thoughtworks.blackhorse.schema.story.FlowProcess

open class MarkdownProcessFormatter : ProcessFormatter {
    override fun process(process: FlowProcess): String {
        val title = process.title()
        val testDouble = process.dependency()
        val input = process.accept ?: process.targetApiScenario?.apiDefinition?.let { "\\> $it" }
        val output = process.reply ?: process.targetApiScenario?.statusDescription?.let { "< $it" }

        return lineOf(
            "- **$title**",
            lineOf(
                input,
                testDouble.takeIf { !input.isNullOrBlank() || !output.isNullOrBlank() },
                output,
            ).prependIndent("  "),
            "----",
            process.nested.mapToLines(this::process)
        )
    }

    private fun ProcessDefinition.title(): String {
        val prefix = "Process $name"
        val relations =
            if (component == dependency) component.name()
            else "${component.name()}, depends on $testDouble<${dependency.name()}>"
        return "$prefix | $relations"
    }

    private fun FlowProcess.title(): String {
        if (definition == null) {
            println("!![WARNING] No process definition between [$startName] and [$targetName].")
            return "Inner Logic | $startName"
        }

        return definition.title()
    }

    private fun FlowProcess.label() =
        definition?.run { "Process $name" } ?: "Inner Logic"

    private fun FlowProcess.dependency() =
        definition?.run {
            "*${component.name()} -> $testDouble<${dependency.name()}>*".takeIf { testDouble != TestDouble.Real }
        }

    override fun diagram(process: FlowProcess): String = lineOf(
        process.go(),
        process.nested.mapToLines(this::diagram),
        process.back()
    )

    open fun FlowProcess.lineTo() = when (definition?.testDouble) {
        null -> "->"
        else -> "-->"
    }

    open fun FlowProcess.lineBack() = when (definition?.testDouble) {
        null -> "->"
        else -> "-->"
    }

    open fun FlowProcess.inputNote(): String {
        val note = lineOf(
            label(),
            // accept ?: reply.takeIf { start == target },
            // targetApiScenario?.let { lineOf(it.apiDefinition, it.request) }
        ).ifEmpty { return "" }
        return "Note right of $startName:" + note.replace("\n", "\\n")
    }

    open fun FlowProcess.outputNote(): String {
        val note = lineOf(
            label(),
            // reply,
            // targetApiScenario?.response
        ).ifEmpty { return "" }
        return "Note left of $targetName:" + note.replace("\n", "\\n")
    }

    private fun FlowProcess.go(): String {
        return lineOf(
            startName + " " + lineTo() + " " + targetName + ":", // + input(),
            inputNote()
        )
    }

    private fun FlowProcess.back(): String? {
        if (start == target) return null

        return lineOf(
            targetName + " " + lineBack() + " " + startName + ":", // + output(),
            outputNote()
        )
    }
}
