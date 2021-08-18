package com.thoughtworks.blackhorse.printer.planuml.formatter

import com.thoughtworks.blackhorse.config.StoryContextHolder
import com.thoughtworks.blackhorse.printer.PdfEngine
import com.thoughtworks.blackhorse.printer.interfaces.TaskFormatter
import com.thoughtworks.blackhorse.printer.markdown.formatter.MarkdownFlowFormatter
import com.thoughtworks.blackhorse.printer.markdown.formatter.lineOf
import com.thoughtworks.blackhorse.printer.markdown.formatter.toLines
import com.thoughtworks.blackhorse.schema.architecture.Component
import com.thoughtworks.blackhorse.schema.architecture.Container
import com.thoughtworks.blackhorse.schema.story.Task
import net.sourceforge.plantuml.FileFormat
import net.sourceforge.plantuml.FileFormatOption
import net.sourceforge.plantuml.SourceStringReader
import java.io.ByteArrayOutputStream
import java.nio.file.Files
import java.nio.file.Path
import java.util.UUID
import kotlin.io.path.nameWithoutExtension

class PlantumlFlowFormatter(
    private val taskFormatter: TaskFormatter,
    private val pdfEngine: PdfEngine = PdfEngine.DEFAULT
) : MarkdownFlowFormatter(taskFormatter) {

    override fun taskDiagram(tasks: List<Task>) =
        generateUml(
            pdfEngine,
            StoryContextHolder.distPath(),
            { uml(tasks) },
            StoryContextHolder::getLocalStoryTempFile
        )

    private fun uml(tasks: List<Task>) = lineOf(
        "@startuml",
        """
        skinparam sequence {
            BoxBorderColor WHITE
        }
        """.trimIndent(),
        // "hide footbox",
        participants(tasks),
        tasks(tasks),
        "@enduml"
    )

    private fun participants(tasks: List<Task>) =
        LinkedHashSet<Component>()
            .apply { tasks.map { addAll(it.components()) } }
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

    private fun tasks(tasks: List<Task>) =
        tasks.map(taskFormatter::diagram).toLines()
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
