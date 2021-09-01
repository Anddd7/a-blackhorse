package com.thoughtworks.blackhorse.printer.markdown.formatter

import com.thoughtworks.blackhorse.config.TitleLanguage
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
            " ",
            components(groupedComponents),
            processes(container.getAllProcessDefs()),
        )
    }

    private fun Container.printTechStack() = techStack.highlight("Tech Stack", TechStack::name)
    private fun Component.printTechStack() = techStack.highlight("Tech Stack", TechStack::name)
    private fun Container.printOwner() = owner.highlight("Owner", Member::name)

    private fun <T> List<T>.highlight(label: String, transform: (T) -> String): String? {
        if (isEmpty()) return null

        return joinToString(", ", "$label: **[", "]**", transform = transform)
    }

    open fun components(groupedComponents: List<Pair<ComponentLayer, List<Component>>>) =
        groupedComponents.mapToLines { (layer, components) ->
            lineOf(
                "- " + layer.value,
                components.mapNotNull { it.printTechStack() }.toLines().prependIndent("  - ")
            )
        }

    private fun processes(processes: List<ProcessDef>): String? {
        if (processes.isEmpty()) return null

        return lineOf(
            "#### ${TitleLanguage.getProcessesTitle()}",
            processes.mapToLines(this::processLine)
        )
    }

    private fun processLine(process: ProcessDef): String {
        val id = process.name
        val component = process.component.simpleName()
        val dependency = if (process.isIntra()) process.dependency.simpleName() else process.dependency.name()
        val testDouble = process.testDouble
        val testFramework = process.testFramework?.let { "[$it]" } ?: ""

        return lineOf(
            "##### ${TitleLanguage.getProcessTitle()} $id | $component => $testDouble\\<$dependency>$testFramework",
            process.description
        )
    }
}
