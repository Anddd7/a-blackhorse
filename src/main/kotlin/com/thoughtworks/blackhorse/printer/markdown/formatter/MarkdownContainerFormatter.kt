package com.thoughtworks.blackhorse.printer.markdown.formatter

import com.thoughtworks.blackhorse.printer.interfaces.ContainerFormatter
import com.thoughtworks.blackhorse.schema.architecture.Container
import com.thoughtworks.blackhorse.schema.architecture.ProcessDefinition

open class MarkdownContainerFormatter : ContainerFormatter {
    override fun container(container: Container) =
        lineOf(
            "### " + container.name(),
            container.getAllProcessDefs().mapToLines { processLine(container.name(), it) },
        )

    private fun processLine(containerName: String, definition: ProcessDefinition): String {
        val id = definition.name
        val component = definition.component.name().substringAfter("$containerName.")
        val dependency = definition.dependency.name().substringAfter("$containerName.")
        val testDouble = definition.testDouble

        return lineOf(
            "##### Process $id | $component => $testDouble\\<$dependency>",
            definition.description
        )
    }
}
