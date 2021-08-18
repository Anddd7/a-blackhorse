package com.thoughtworks.blackhorse.printer.planuml.formatter

import com.thoughtworks.blackhorse.config.ProjectContextHolder
import com.thoughtworks.blackhorse.printer.PdfEngine
import com.thoughtworks.blackhorse.printer.markdown.formatter.MarkdownContainerFormatter
import com.thoughtworks.blackhorse.printer.markdown.formatter.lineOf
import com.thoughtworks.blackhorse.printer.markdown.formatter.mapToLines
import com.thoughtworks.blackhorse.schema.architecture.Component
import com.thoughtworks.blackhorse.schema.architecture.attributes.ComponentLayer
import com.thoughtworks.blackhorse.schema.architecture.attributes.TechStack

open class PlantumlContainerFormatter(
    private val pdfEngine: PdfEngine = PdfEngine.DEFAULT
) : MarkdownContainerFormatter() {

    override fun components(groupedComponents: List<Pair<ComponentLayer, List<Component>>>) =
        diagram(groupedComponents) ?: ""

    private fun diagram(groups: List<Pair<ComponentLayer, List<Component>>>): String? {
        if (groups.isEmpty()) return null

        return generateUml(
            pdfEngine,
            ProjectContextHolder.distPath(),
            { uml(groups) },
            ProjectContextHolder::getLocalStoryTempFile
        )
    }

    private fun uml(groups: List<Pair<ComponentLayer, List<Component>>>) = lineOf(
        "@startuml",
        groups.mapToLines(this::group),
        "@enduml"
    )

    private fun group(group: Pair<ComponentLayer, List<Component>>): String {
        val layer = group.first
        val components = group.second

        return lineOf(
            "package \"${layer.value}\" {",
            components.mapToLines(this::component).prependIndent("  "),
            "}"
        )
    }

    private fun component(component: Component): String {
        val name = component.name().substringAfter(".")
        val techStack = component.techStack.joinToString(",", "[", "]", transform = TechStack::name)

        return """
                component $name [
                    $name
                    $techStack 
                    ${component.responsibility}
                ]
        """.trimIndent()
    }
}
