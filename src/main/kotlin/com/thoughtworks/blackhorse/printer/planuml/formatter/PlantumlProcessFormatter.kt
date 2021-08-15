package com.thoughtworks.blackhorse.printer.planuml.formatter

import com.thoughtworks.blackhorse.printer.markdown.formatter.MarkdownProcessFormatter
import com.thoughtworks.blackhorse.schema.architecture.attributes.TestDouble
import com.thoughtworks.blackhorse.schema.story.FlowProcess

class PlantumlProcessFormatter : MarkdownProcessFormatter() {
    override fun FlowProcess.lineTo() = when (definition?.testDouble) {
        null -> "->"
        TestDouble.Dummy -> "-->"
        else -> "->o"
    }

    override fun FlowProcess.lineBack() = when (definition?.testDouble) {
        null -> "->"
        TestDouble.Dummy -> "-->"
        else -> {
            if (targetApiScenario != null && !targetApiScenario.statusCode.isSuccess()) "x->"
            else "o->"
        }
    }
}
