package com.thoughtworks.blackhorse.printer.markdown.formatter

import com.thoughtworks.blackhorse.printer.interfaces.ArchitectureFormatter
import com.thoughtworks.blackhorse.schema.architecture.Architecture
import com.thoughtworks.blackhorse.schema.architecture.Container
import com.thoughtworks.blackhorse.schema.architecture.ProcessDefinition
import com.thoughtworks.blackhorse.schema.architecture.attributes.ContainerLayer

class MarkdownArchitectureFormatter : ArchitectureFormatter {

    override fun architecture(architecture: Architecture): String {
        val groups = architecture.containers
            .groupBy { it.layer }.entries
            .sortedBy { it.key.order() }
            .map { it.key to it.value.sortedBy(Container::id) }

        return lineOf(
            "# Architecture Map of ${architecture.projectName}",
            "##### ChangeLogs",
            architecture.changelogs,
            groups.mapToLines { layer(it.first, it.second.sortedBy(Container::id)) }
        )
    }

    private fun layer(containerLayer: ContainerLayer, containers: List<Container>) =
        lineOf(
            "## ${containerLayer.value()}",
            containers.mapToLines(::container)
        )

    private fun container(container: Container) =
        lineOf(
            "### " + container.name(),
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
