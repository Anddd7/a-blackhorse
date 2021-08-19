package com.thoughtworks.blackhorse.printer

import com.thoughtworks.blackhorse.printer.interfaces.ArchitecturePrinter
import com.thoughtworks.blackhorse.printer.interfaces.StoryPrinter
import com.thoughtworks.blackhorse.printer.markdown.MarkdownArchitecturePrinter
import com.thoughtworks.blackhorse.printer.markdown.MarkdownStoryPrinter
import com.thoughtworks.blackhorse.printer.markdown.formatter.MarkdownAcceptanceCriteriaFormatter
import com.thoughtworks.blackhorse.printer.markdown.formatter.MarkdownApiSchemaFormatter
import com.thoughtworks.blackhorse.printer.markdown.formatter.MarkdownArchitectureFormatter
import com.thoughtworks.blackhorse.printer.markdown.formatter.MarkdownContainerFormatter
import com.thoughtworks.blackhorse.printer.markdown.formatter.MarkdownFlowFormatter
import com.thoughtworks.blackhorse.printer.markdown.formatter.MarkdownStoryFormatter
import com.thoughtworks.blackhorse.printer.markdown.formatter.MarkdownTaskFormatter
import com.thoughtworks.blackhorse.printer.planuml.formatter.PlantumlContainerFormatter
import com.thoughtworks.blackhorse.printer.planuml.formatter.PlantumlFlowFormatter
import com.thoughtworks.blackhorse.printer.planuml.formatter.PlantumlTaskFormatter

enum class PrinterOption(

) {
    /**
     * export as a markdown file, using built-in js-sequence to display the sequence diagram
     */
    MARKDOWN_TYPORA,

    /**
     * export as a markdown file, with injected plantuml sequence diagram
     */
    MARKDOWN_TYPORA_PLANTUML,

    /**
     * export as a pdf file (using pandoc) with plantuml
     */
    PDF_PLANTUML,

    /**
     * export as a pdf file and upload to jira
     */
    JIRA_ATTACHMENT;

    val storyPrinter: StoryPrinter by lazy {
        val asf = MarkdownApiSchemaFormatter()
        val cf = when (this) {
            MARKDOWN_TYPORA -> MarkdownContainerFormatter()
            MARKDOWN_TYPORA_PLANTUML -> PlantumlContainerFormatter()
            else -> PlantumlContainerFormatter(pdfEngine = PdfEngine.LATEX)
        }
        val tf = when (this) {
            MARKDOWN_TYPORA -> MarkdownTaskFormatter()
            else -> PlantumlTaskFormatter()
        }
        val ff = when (this) {
            MARKDOWN_TYPORA -> MarkdownFlowFormatter(tf)
            MARKDOWN_TYPORA_PLANTUML -> PlantumlFlowFormatter(tf)
            else -> PlantumlFlowFormatter(tf, pdfEngine = PdfEngine.LATEX)
        }
        val acf = MarkdownAcceptanceCriteriaFormatter(ff)
        val sf = MarkdownStoryFormatter(acf, asf, cf)

        MarkdownStoryPrinter(sf)
    }
    val architecturePrinter: ArchitecturePrinter by lazy {
        val cf = when (this) {
            MARKDOWN_TYPORA -> MarkdownContainerFormatter()
            MARKDOWN_TYPORA_PLANTUML -> PlantumlContainerFormatter()
            else -> PlantumlContainerFormatter(pdfEngine = PdfEngine.LATEX)
        }
        val af = when (this) {
            MARKDOWN_TYPORA -> MarkdownArchitectureFormatter(cf)
            else -> MarkdownArchitectureFormatter(cf)
        }

        MarkdownArchitecturePrinter(af)
    }
}

enum class PdfEngine {
    DEFAULT, LATEX
}
