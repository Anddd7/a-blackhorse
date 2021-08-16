package com.thoughtworks.blackhorse.printer.markdown

import com.thoughtworks.blackhorse.config.ProjectContext
import com.thoughtworks.blackhorse.printer.interfaces.ArchitectureFormatter
import com.thoughtworks.blackhorse.printer.interfaces.ArchitecturePrinter
import com.thoughtworks.blackhorse.schema.architecture.Architecture
import com.thoughtworks.blackhorse.utils.FileExtension
import org.slf4j.LoggerFactory
import kotlin.system.exitProcess

class MarkdownArchitecturePrinter(
    private val formatter: ArchitectureFormatter
) : ArchitecturePrinter {
    private val log = LoggerFactory.getLogger(this.javaClass)

    override fun start(architecture: Architecture) {
        val context = ProjectContext.load(architecture.projectName)
        val content = formatter.architecture(architecture)
        try {
            val file = context.createProjectFileIfAbsent("architecture-${architecture.version}.md")
            FileExtension.writeToMarkdown(file, content)
        } catch (e: FileAlreadyExistsException) {
            log.error(
                """
                    There is a existing Architecture with the same version, pls update the version and add comments into changes if you updated it
                """.trimIndent()
            )
            exitProcess(0)
        }
    }
}
