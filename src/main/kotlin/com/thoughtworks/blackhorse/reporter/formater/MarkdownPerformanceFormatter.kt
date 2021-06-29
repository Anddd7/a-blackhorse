package com.thoughtworks.blackhorse.reporter.formater

import com.thoughtworks.blackhorse.printer.markdown.formatter.lineOf
import com.thoughtworks.blackhorse.printer.markdown.formatter.mapIndexedToLines
import com.thoughtworks.blackhorse.printer.markdown.formatter.mapToLines
import com.thoughtworks.blackhorse.reporter.interfaces.PerformanceColumns
import com.thoughtworks.blackhorse.reporter.interfaces.PerformanceFormatter
import com.thoughtworks.blackhorse.schema.performance.StoryPerformance

object MarkdownPerformanceFormatter : PerformanceFormatter {
    override fun performances(items: List<StoryPerformance>): String =
        lineOf(
            PerformanceColumns.columns.joinToTable { it.name },
            PerformanceColumns.columns.joinToTable { "---" },
            items.mapToLines { performance(it) },
        )

    private fun performance(story: StoryPerformance): String =
        story.processes.mapIndexedToLines { index, process ->
            PerformanceColumns.columns.joinToTable { it.extract(index, story, process) }
        }

    private fun <T> Iterable<T>.joinToTable(transform: ((T) -> CharSequence)? = null): String =
        joinToString(" | ", "| ", " |", transform = transform)
}
