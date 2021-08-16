package com.thoughtworks.blackhorse.printer

import com.thoughtworks.blackhorse.printer.interfaces.ArchitecturePrinter
import com.thoughtworks.blackhorse.printer.interfaces.StoryPrinter
import com.thoughtworks.blackhorse.printer.jira.JiraStoryPrinter
import com.thoughtworks.blackhorse.printer.markdown.MarkdownArchitecturePrinter
import com.thoughtworks.blackhorse.printer.markdown.MarkdownStoryPrinter
import com.thoughtworks.blackhorse.printer.markdown.formatter.MarkdownAcceptanceCriteriaFormatter
import com.thoughtworks.blackhorse.printer.markdown.formatter.MarkdownApiSchemaFormatter
import com.thoughtworks.blackhorse.printer.markdown.formatter.MarkdownArchitectureFormatter
import com.thoughtworks.blackhorse.printer.markdown.formatter.MarkdownContainerFormatter
import com.thoughtworks.blackhorse.printer.markdown.formatter.MarkdownFlowFormatter
import com.thoughtworks.blackhorse.printer.markdown.formatter.MarkdownProcessFormatter
import com.thoughtworks.blackhorse.printer.markdown.formatter.MarkdownStoryFormatter
import com.thoughtworks.blackhorse.printer.pdf.PandocPdfStoryPrinter
import com.thoughtworks.blackhorse.printer.planuml.formatter.PlantumlArchitectureFormatter
import com.thoughtworks.blackhorse.printer.planuml.formatter.PlantumlContainerFormatter
import com.thoughtworks.blackhorse.printer.planuml.formatter.PlantumlFlowFormatter
import com.thoughtworks.blackhorse.printer.planuml.formatter.PlantumlProcessFormatter

enum class PrinterOption(
    val storyPrinter: StoryPrinter,
    val architecturePrinter: ArchitecturePrinter,
) {
    /**
     * export as a markdown file, using built-in js-sequence to display the sequence diagram
     */
    MARKDOWN_TYPORA(
        MarkdownStoryPrinter(
            MarkdownStoryFormatter(
                MarkdownAcceptanceCriteriaFormatter(
                    MarkdownFlowFormatter(
                        MarkdownProcessFormatter()
                    ),
                ),
                MarkdownApiSchemaFormatter(),
            ),
        ),
        MarkdownArchitecturePrinter(
            MarkdownArchitectureFormatter(
                MarkdownContainerFormatter()
            )
        )
    ),

    /**
     * export as a markdown file, with injected plantuml sequence diagram
     */
    MARKDOWN_TYPORA_PLANTUML(
        MarkdownStoryPrinter(
            MarkdownStoryFormatter(
                MarkdownAcceptanceCriteriaFormatter(
                    PlantumlFlowFormatter(
                        PlantumlProcessFormatter()
                    ),
                ),
                MarkdownApiSchemaFormatter(),
            ),
        ),
        MarkdownArchitecturePrinter(
            PlantumlArchitectureFormatter(
                PlantumlContainerFormatter()
            )
        )
    ),

    /**
     * export as a pdf file (using pandoc) with plantuml
     */
    PDF_PLANTUML(
        PandocPdfStoryPrinter(
            MarkdownStoryFormatter(
                MarkdownAcceptanceCriteriaFormatter(
                    PlantumlFlowFormatter(
                        PlantumlProcessFormatter(),
                        pdfEngine = PdfEngine.LATEX,
                    ),
                ),
                MarkdownApiSchemaFormatter(),
            ),
        ),
        MarkdownArchitecturePrinter(
            PlantumlArchitectureFormatter(
                PlantumlContainerFormatter(
                    pdfEngine = PdfEngine.LATEX,
                ),
                pdfEngine = PdfEngine.LATEX,
            )
        )
    ),

    /**
     * export as a pdf file and upload to jira
     */
    JIRA_ATTACHMENT(
        JiraStoryPrinter(
            MarkdownStoryFormatter(
                MarkdownAcceptanceCriteriaFormatter(
                    PlantumlFlowFormatter(
                        PlantumlProcessFormatter(),
                        pdfEngine = PdfEngine.LATEX,
                    ),
                ),
                MarkdownApiSchemaFormatter(),
            ),
        ),
        MarkdownArchitecturePrinter(
            PlantumlArchitectureFormatter(
                PlantumlContainerFormatter(
                    pdfEngine = PdfEngine.LATEX,
                ),
                pdfEngine = PdfEngine.LATEX,
            )
        )
    ),
    ;
}

enum class PdfEngine {
    DEFAULT, LATEX
}
