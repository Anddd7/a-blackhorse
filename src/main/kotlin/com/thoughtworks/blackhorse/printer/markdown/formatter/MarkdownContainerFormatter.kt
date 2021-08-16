package com.thoughtworks.blackhorse.printer.markdown.formatter

import com.thoughtworks.blackhorse.printer.interfaces.ArchitectureContainerFormatter
import com.thoughtworks.blackhorse.schema.architecture.Container
import com.thoughtworks.blackhorse.schema.architecture.ProcessDefinition

class MarkdownContainerFormatter : ArchitectureContainerFormatter {
    private val title = "Project Process Definition"

    override fun anchors(items: Set<Container>): String = toAnchorLink(title)
    override fun containers(items: Set<Container>) =
        lineOf(
            "### $title",
            items.mapToLines(this::container)
        )

    private fun container(container: Container) =
        lineOf(
            "#### " + container.name(),
            container.getAllProcessDefs().map { processLine(container.name(), it) }.toLines(),
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
