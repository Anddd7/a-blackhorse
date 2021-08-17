package com.thoughtworks.blackhorse.printer.planuml.formatter

import com.thoughtworks.blackhorse.config.ProjectContextHolder
import com.thoughtworks.blackhorse.printer.PdfEngine
import com.thoughtworks.blackhorse.printer.interfaces.ContainerFormatter
import com.thoughtworks.blackhorse.printer.markdown.formatter.MarkdownArchitectureFormatter
import com.thoughtworks.blackhorse.printer.markdown.formatter.lineOf
import com.thoughtworks.blackhorse.printer.markdown.formatter.mapToLines
import com.thoughtworks.blackhorse.printer.markdown.formatter.toLines
import com.thoughtworks.blackhorse.schema.architecture.Container
import com.thoughtworks.blackhorse.schema.architecture.attributes.ContainerLayer

class PlantumlArchitectureFormatter(
    containerFormatter: ContainerFormatter,
    private val pdfEngine: PdfEngine = PdfEngine.DEFAULT
) : MarkdownArchitectureFormatter(containerFormatter) {

    override fun containers(groups: List<Pair<ContainerLayer, List<Container>>>) =
        lineOf(
            diagram(groups),
            super.containers(groups),
        )

    private fun diagram(groups: List<Pair<ContainerLayer, List<Container>>>): String? {
        if (groups.isEmpty()) return null

        return generateUml(
            pdfEngine,
            ProjectContextHolder.distPath(),
            {
                val uml = uml(groups)
                println(uml)
                uml
            },
            ProjectContextHolder::getLocalStoryTempFile
        )
    }

    private fun uml(groups: List<Pair<ContainerLayer, List<Container>>>) = lineOf(
        "@startuml",
        groups.mapToLines(this::group),
        accumulate(groups.map { it.first }),
        "@enduml"
    )

    private fun accumulate(list: List<ContainerLayer>): String {
        val result = mutableListOf<String>()
        for (i in 1 until list.size) {
            val prev = list[i - 1]
            val curr = list[i]
            result.add("\"${prev.value()}\" -d- \"${curr.value()}\"")
        }
        return result.toLines()
    }

    private fun group(group: Pair<ContainerLayer, List<Container>>): String {
        val layer = group.first
        val containers = group.second

        return lineOf(
            "package \"${layer.value()}\" {",
            containers.mapToLines(this::container).prependIndent("  "),
            "}"
        )
    }

    private fun container(container: Container) =
        """
            component ${container.name()} [
              ${container.name()}
              ${container.responsibility}
            ]
        """.trimIndent()
}
