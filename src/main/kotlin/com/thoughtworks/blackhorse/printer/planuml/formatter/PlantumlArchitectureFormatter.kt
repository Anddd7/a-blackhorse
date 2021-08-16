package com.thoughtworks.blackhorse.printer.planuml.formatter

import com.thoughtworks.blackhorse.config.ProjectContext
import com.thoughtworks.blackhorse.printer.PdfEngine
import com.thoughtworks.blackhorse.printer.markdown.formatter.MarkdownArchitectureFormatter
import com.thoughtworks.blackhorse.printer.markdown.formatter.lineOf
import com.thoughtworks.blackhorse.printer.markdown.formatter.mapToLines
import com.thoughtworks.blackhorse.schema.architecture.Architecture
import com.thoughtworks.blackhorse.schema.architecture.Container
import com.thoughtworks.blackhorse.schema.architecture.attributes.ContainerLayer
import net.sourceforge.plantuml.FileFormat
import net.sourceforge.plantuml.FileFormatOption
import net.sourceforge.plantuml.SourceStringReader
import java.io.ByteArrayOutputStream
import java.nio.file.Files
import java.nio.file.Path
import java.util.UUID
import kotlin.io.path.nameWithoutExtension

class PlantumlArchitectureFormatter(
    private val pdfEngine: PdfEngine = PdfEngine.DEFAULT
) : MarkdownArchitectureFormatter() {

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

    private fun diagram(projectContext: ProjectContext, groups: List<Pair<ContainerLayer, List<Container>>>): String {
        val fileFormat = when (pdfEngine) {
            PdfEngine.DEFAULT -> FileFormat.SVG
            PdfEngine.LATEX -> FileFormat.PNG
        }
        val padding = when (pdfEngine) {
            PdfEngine.DEFAULT -> ""
            PdfEngine.LATEX -> "> "
        }

        val umlSource = uml(groups)
        val tempFile = generateTempFile(projectContext, umlSource, fileFormat)
        val relativePath = projectContext.distPath.relativize(tempFile)

        return "$padding![${tempFile.fileName.nameWithoutExtension}]($relativePath)"
    }

    private fun uml(groups: List<Pair<ContainerLayer, List<Container>>>) = lineOf(
        "@startuml",
        """
        skinparam sequence {
            BoxBorderColor WHITE
        }
        """.trimIndent(),
        // "hide footbox",
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

    private fun container(container: Container) = "[${container.name()}]"

    private fun generateTempFile(projectContext: ProjectContext, source: String, fileFormat: FileFormat): Path {
        val reader = SourceStringReader(source)
        val os = ByteArrayOutputStream().apply {
            use {
                reader.outputImage(it, FileFormatOption(fileFormat))
            }
        }
        val fileName = UUID.randomUUID().toString()
        val temp = projectContext.getProjectTempFile("$fileName.${fileFormat.ext()}")
        Files.write(temp, os.toByteArray())
        return temp
    }

    private fun FileFormat.ext() = name.lowercase()
}
