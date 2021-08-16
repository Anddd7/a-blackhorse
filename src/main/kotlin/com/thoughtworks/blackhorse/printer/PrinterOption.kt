package com.thoughtworks.blackhorse.printer

import com.thoughtworks.blackhorse.printer.interfaces.StoryPrinter
import com.thoughtworks.blackhorse.printer.jira.JiraPrinter
import com.thoughtworks.blackhorse.printer.markdown.MarkdownPrinter
import com.thoughtworks.blackhorse.printer.markdown.formatter.MarkdownAcceptanceCriteriaFormatter
import com.thoughtworks.blackhorse.printer.markdown.formatter.MarkdownApiSchemaFormatter
import com.thoughtworks.blackhorse.printer.markdown.formatter.MarkdownContainerFormatter
import com.thoughtworks.blackhorse.printer.markdown.formatter.MarkdownFlowFormatter
import com.thoughtworks.blackhorse.printer.markdown.formatter.MarkdownProcessFormatter
import com.thoughtworks.blackhorse.printer.markdown.formatter.MarkdownStoryFormatter
import com.thoughtworks.blackhorse.printer.pdf.PandocPdfPrinter
import com.thoughtworks.blackhorse.printer.planuml.formatter.PlantumlFlowFormatter
import com.thoughtworks.blackhorse.printer.planuml.formatter.PlantumlProcessFormatter

enum class PrinterOption(
    private val initializer: () -> StoryPrinter,
    val printer: StoryPrinter = initializer(),
) {
    /**
     * export as a markdown file, using built-in js-sequence to display the sequence diagram
     */
    MARKDOWN_TYPORA({
        MarkdownPrinter(
            MarkdownStoryFormatter(
                MarkdownAcceptanceCriteriaFormatter(
                    MarkdownFlowFormatter(
                        MarkdownProcessFormatter()
                    ),
                ),
                MarkdownApiSchemaFormatter(),
            ),
        )
    }),

    /**
     * export as a markdown file, with injected plantuml sequence diagram
     */
    MARKDOWN_TYPORA_PLANTUML({
        MarkdownPrinter(
            MarkdownStoryFormatter(
                MarkdownAcceptanceCriteriaFormatter(
                    PlantumlFlowFormatter(
                        PlantumlProcessFormatter()
                    ),
                ),
                MarkdownApiSchemaFormatter(),
            ),
        )
    }),

    /**
     * export as a pdf file (using pandoc) with plantuml
     */
    PDF_PLANTUML({
        PandocPdfPrinter(
            MarkdownStoryFormatter(
                MarkdownAcceptanceCriteriaFormatter(
                    PlantumlFlowFormatter(
                        PlantumlProcessFormatter(),
                        pdfEngine = PdfEngine.LATEX,
                    ),
                ),
                MarkdownApiSchemaFormatter(),
            ),
        )
    }),

    /**
     * export as a pdf file and upload to jira
     */
    JIRA_ATTACHMENT({
        JiraPrinter(
            MarkdownStoryFormatter(
                MarkdownAcceptanceCriteriaFormatter(
                    PlantumlFlowFormatter(
                        PlantumlProcessFormatter(),
                        pdfEngine = PdfEngine.LATEX,
                    ),
                ),
                MarkdownApiSchemaFormatter(),
            ),
        )
    }),
    ;
}

enum class PdfEngine {
    DEFAULT, LATEX
}
