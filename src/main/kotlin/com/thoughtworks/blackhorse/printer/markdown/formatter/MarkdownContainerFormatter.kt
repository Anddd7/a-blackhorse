package com.thoughtworks.blackhorse.printer.markdown.formatter

import com.thoughtworks.blackhorse.printer.interfaces.ContainerFormatter
import com.thoughtworks.blackhorse.schema.architecture.Component
import com.thoughtworks.blackhorse.schema.architecture.Container
import com.thoughtworks.blackhorse.schema.architecture.ProcessDefinition
import com.thoughtworks.blackhorse.schema.architecture.attributes.ComponentLayer

open class MarkdownContainerFormatter : ContainerFormatter {
    override fun container(container: Container): String {
        val groupedComponents = container.getComponents()
            .groupBy(Component::layer)
            .entries.sortedBy { it.key.order }
            .map { it.key to it.value }

        return lineOf(
            "### " + container.name(),
            container.responsibility,
            components(groupedComponents),
            processes(container.name(), container.getAllProcessDefs())
        )
    }

    open fun components(groupedComponents: List<Pair<ComponentLayer, List<Component>>>) =
        groupedComponents.mapToLines { (layer, components) ->
            lineOf(
                "- " + layer.value,
                components.mapToLines(Component::name).prependIndent("  - ")
            )
        }

    private fun processes(containerName: String, processDefs: List<ProcessDefinition>): String? {
        if (processDefs.isEmpty()) return null

        return lineOf(
            "#### Processes",
            processDefs.mapToLines { processLine(containerName, it) }
        )
    }

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
