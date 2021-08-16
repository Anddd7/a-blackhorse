package com.thoughtworks.blackhorse.printer.planuml.formatter

import com.thoughtworks.blackhorse.config.ProjectContext
import com.thoughtworks.blackhorse.printer.PdfEngine
import com.thoughtworks.blackhorse.printer.interfaces.ContainerFormatter
import com.thoughtworks.blackhorse.printer.markdown.formatter.MarkdownArchitectureFormatter
import com.thoughtworks.blackhorse.printer.markdown.formatter.lineOf
import com.thoughtworks.blackhorse.printer.markdown.formatter.mapToLines
import com.thoughtworks.blackhorse.schema.architecture.Architecture
import com.thoughtworks.blackhorse.schema.architecture.Container
import com.thoughtworks.blackhorse.schema.architecture.attributes.ContainerLayer

class PlantumlArchitectureFormatter(
    containerFormatter: ContainerFormatter,
    private val pdfEngine: PdfEngine = PdfEngine.DEFAULT
) : MarkdownArchitectureFormatter(containerFormatter) {

    override fun architecture(architecture: Architecture): String {
        val groups = getGroupedContainers(architecture)

        return lineOf(
            "# Architecture Map of ${architecture.projectName}",
            "##### ChangeLogs",
            architecture.changelogs,
            diagram(ProjectContext.load(architecture.projectName), groups),
            groups.mapToLines { layer(it.first, it.second.sortedBy(Container::id)) }
        )
    }

    private fun diagram(projectContext: ProjectContext, groups: List<Pair<ContainerLayer, List<Container>>>) =
        generateUml(
            pdfEngine,
            projectContext.distPath,
            { uml(groups) },
            projectContext::getProjectTempFile
        )

    private fun uml(groups: List<Pair<ContainerLayer, List<Container>>>) = lineOf(
        "@startuml",
        groups.mapToLines(this::group),
        "@enduml"
    )

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
