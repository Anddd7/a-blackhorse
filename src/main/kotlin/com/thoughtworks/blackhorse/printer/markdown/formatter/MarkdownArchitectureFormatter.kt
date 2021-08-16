package com.thoughtworks.blackhorse.printer.markdown.formatter

import com.thoughtworks.blackhorse.printer.interfaces.ArchitectureFormatter
import com.thoughtworks.blackhorse.printer.interfaces.ContainerFormatter
import com.thoughtworks.blackhorse.schema.architecture.Architecture
import com.thoughtworks.blackhorse.schema.architecture.Container
import com.thoughtworks.blackhorse.schema.architecture.attributes.ContainerLayer

open class MarkdownArchitectureFormatter(
    private val containerFormatter: ContainerFormatter,
) : ArchitectureFormatter {

    override fun architecture(architecture: Architecture) =
        lineOf(
            "# Architecture Map of ${architecture.projectName}",
            "##### ChangeLogs",
            architecture.changelogs,
            getGroupedContainers(architecture).mapToLines { layer(it.first, it.second.sortedBy(Container::id)) }
        )

    protected fun getGroupedContainers(architecture: Architecture) =
        architecture.containers
            .groupBy { it.layer }.entries
            .sortedBy { it.key.order() }
            .map { it.key to it.value.sortedBy(Container::id) }

    protected fun layer(containerLayer: ContainerLayer, containers: List<Container>) =
        lineOf(
            "## ${containerLayer.value()}",
            containers.mapToLines(containerFormatter::container)
        )
}
