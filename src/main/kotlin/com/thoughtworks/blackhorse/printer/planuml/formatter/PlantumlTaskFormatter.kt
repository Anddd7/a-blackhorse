package com.thoughtworks.blackhorse.printer.planuml.formatter

import com.thoughtworks.blackhorse.printer.markdown.formatter.MarkdownTaskFormatter
import com.thoughtworks.blackhorse.schema.architecture.attributes.TestDouble
import com.thoughtworks.blackhorse.schema.story.Task

class PlantumlTaskFormatter : MarkdownTaskFormatter() {
    override fun Task.lineTo() = when (process?.testDouble) {
        null -> "->"
        TestDouble.Dummy -> "-->"
        else -> "->o"
    }

    override fun Task.lineBack() = when (process?.testDouble) {
        null -> "->"
        TestDouble.Dummy -> "-->"
        else -> {
            if (targetApiScenario != null && !targetApiScenario.statusCode.isSuccess()) "x->"
            else "o->"
        }
    }
}
