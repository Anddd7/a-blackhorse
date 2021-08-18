package com.thoughtworks.blackhorse.printer.markdown.formatter

import com.thoughtworks.blackhorse.printer.interfaces.ContainerFormatter
import com.thoughtworks.blackhorse.schema.architecture.Component
import com.thoughtworks.blackhorse.schema.architecture.Container
import com.thoughtworks.blackhorse.schema.architecture.ProcessDef
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

    private fun processes(processes: List<ProcessDef>): String? {
        if (processes.isEmpty()) return null

        return lineOf(
            "#### Processes",
            processes.mapToLines(this::processLine)
        )
    }

    private fun processLine(process: ProcessDef): String {
        val id = process.name
        val component = process.component.simpleName()
        val dependency = process.dependency.simpleName()
        val testDouble = process.testDouble

        return lineOf(
            "##### Process $id | $component => $testDouble\\<$dependency>",
            process.description
        )
    }
}
