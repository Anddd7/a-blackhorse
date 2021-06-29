package com.thoughtworks.blackhorse.reporter.formater

import com.thoughtworks.blackhorse.printer.markdown.formatter.lineOf
import com.thoughtworks.blackhorse.printer.markdown.formatter.mapToLines
import com.thoughtworks.blackhorse.reporter.interfaces.PerformanceColumns
import com.thoughtworks.blackhorse.reporter.interfaces.PerformanceFormatter
import com.thoughtworks.blackhorse.schema.performance.StoryPerformance

object MarkdownPerformanceFormatter : PerformanceFormatter {
    override fun performances(items: List<StoryPerformance>) =
        lineOf(
            PerformanceColumns.columns.joinToTable { it.name },
            PerformanceColumns.columns.joinToTable { "---" },
            items.mapToLines { performance ->
                performance.withTaskIds().mapToLines(this::performance)
            },
        )

    private fun performance(pair: Pair<StoryPerformance, Int>) =
        PerformanceColumns.columns.joinToTable { it.extract(pair.first, pair.second) }

    private fun <T> Iterable<T>.joinToTable(transform: ((T) -> CharSequence)? = null) =
        joinToString(" | ", "| ", " |", transform = transform)
}
