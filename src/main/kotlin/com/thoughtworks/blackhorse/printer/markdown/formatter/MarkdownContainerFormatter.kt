package com.thoughtworks.blackhorse.printer.markdown.formatter

import com.thoughtworks.blackhorse.printer.interfaces.ContainerFormatter
import com.thoughtworks.blackhorse.schema.architecture.Component
import com.thoughtworks.blackhorse.schema.architecture.Container
import com.thoughtworks.blackhorse.schema.architecture.ProcessDefinition
import com.thoughtworks.blackhorse.schema.architecture.attributes.ComponentLayer
import com.thoughtworks.blackhorse.schema.architecture.attributes.TechStack
import com.thoughtworks.blackhorse.schema.performance.attributes.Member

open class MarkdownContainerFormatter : ContainerFormatter {
    override fun container(container: Container): String {
        val groupedComponents = container.getComponents()
            .groupBy(Component::layer)
            .entries.sortedBy { it.key.order }
            .map { it.key to it.value }

        return lineOf(
            "### " + container.name(),
            container.responsibility,
            container.printTechStack(),
            container.printOwner(),
            components(groupedComponents),
            processes(container.getAllProcessDefs())
        )
    }

    private fun Container.printTechStack() = techStack.print("Tech Stack", TechStack::name)
    private fun Component.printTechStack() = techStack.print("Tech Stack", TechStack::name)
    private fun Container.printOwner() = owner.print("Owner", Member::name)

    private fun <T> List<T>.print(prefix: String, transform: (T) -> String) =
        joinToString(", ", "$prefix: [", "]", transform = transform)

    open fun components(groupedComponents: List<Pair<ComponentLayer, List<Component>>>) =
        groupedComponents.mapToLines { (layer, components) ->
            lineOf(
                "- " + layer.value,
                components.mapToLines { it.printTechStack() }.prependIndent("  - ")
            )
        }

    private fun processes(processDefs: List<ProcessDefinition>): String? {
        if (processDefs.isEmpty()) return null

        return lineOf(
            "#### Processes",
            processDefs.mapToLines(this::processLine)
        )
    }

    private fun processLine(definition: ProcessDefinition): String {
        val id = definition.name
        val component = definition.component.simpleName()
        val dependency = definition.dependency.simpleName()
        val testDouble = definition.testDouble

        return lineOf(
            "##### Process $id | $component => $testDouble\\<$dependency>",
            definition.description
        )
    }
}
