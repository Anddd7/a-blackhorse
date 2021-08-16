package com.thoughtworks.blackhorse.printer.planuml.formatter

import com.thoughtworks.blackhorse.config.StoryContextHolder
import com.thoughtworks.blackhorse.printer.PdfEngine
import com.thoughtworks.blackhorse.printer.interfaces.ProcessFormatter
import com.thoughtworks.blackhorse.printer.markdown.formatter.MarkdownFlowFormatter
import com.thoughtworks.blackhorse.printer.markdown.formatter.lineOf
import com.thoughtworks.blackhorse.printer.markdown.formatter.toLines
import com.thoughtworks.blackhorse.schema.architecture.Component
import com.thoughtworks.blackhorse.schema.architecture.Container
import com.thoughtworks.blackhorse.schema.story.FlowProcess
import net.sourceforge.plantuml.FileFormat
import net.sourceforge.plantuml.FileFormatOption
import net.sourceforge.plantuml.SourceStringReader
import java.io.ByteArrayOutputStream
import java.nio.file.Files
import java.nio.file.Path
import java.util.UUID
import kotlin.io.path.nameWithoutExtension

class PlantumlFlowFormatter(
    private val processFormatter: ProcessFormatter,
    private val pdfEngine: PdfEngine = PdfEngine.DEFAULT
) : MarkdownFlowFormatter(processFormatter) {

    override fun processDiagram(processes: List<FlowProcess>) =
        generateUml(
            pdfEngine,
            StoryContextHolder.distPath(),
            { uml(processes) },
            StoryContextHolder::getLocalStoryTempFile
        )

    private fun uml(processes: List<FlowProcess>) = lineOf(
        "@startuml",
        """
        skinparam sequence {
            BoxBorderColor WHITE
        }
        """.trimIndent(),
        // "hide footbox",
        participants(processes),
        processes(processes),
        "@enduml"
    )

    private fun participants(processes: List<FlowProcess>) =
        LinkedHashSet<Component>()
            .apply { processes.map { addAll(it.components()) } }
            .groupBy(Component::getContainer)
            .map { (container, nodes) -> container.box(nodes) }
            .toLines()

    private fun Container.box(components: List<Component>) = lineOf(
        "box ${name()} ${boxColor()}",
        nodes(components),
        "end box"
    )

    // TODO style
    private fun Container.boxColor() = " "

    private fun nodes(components: List<Component>) =
        components.map { node -> "${node.participant()} ${node.name()}" }.toLines()

    // TODO style
    private fun Component.participant() = "entity"

    private fun processes(processes: List<FlowProcess>) =
        processes.map(processFormatter::diagram).toLines()
}

private fun FileFormat.ext() = name.lowercase()

fun generateUml(
    pdfEngine: PdfEngine,
    distPath: Path,
    uml: () -> String,
    createTempFile: (String) -> Path
): String {
    val fileFormat = when (pdfEngine) {
        PdfEngine.DEFAULT -> FileFormat.SVG
        PdfEngine.LATEX -> FileFormat.PNG
    }
    val padding = when (pdfEngine) {
        PdfEngine.DEFAULT -> ""
        PdfEngine.LATEX -> "> "
    }
    val source = uml()
    val fileName = "${UUID.randomUUID()}.${fileFormat.ext()}"
    val tempFile = createTempFile(fileName)
    val reader = SourceStringReader(source)
    val os = ByteArrayOutputStream().apply {
        use {
            reader.outputImage(it, FileFormatOption(fileFormat))
        }
    }
    Files.write(tempFile, os.toByteArray())

    val relativePath = distPath.relativize(tempFile)

    return "$padding![${tempFile.fileName.nameWithoutExtension}]($relativePath)"
}
