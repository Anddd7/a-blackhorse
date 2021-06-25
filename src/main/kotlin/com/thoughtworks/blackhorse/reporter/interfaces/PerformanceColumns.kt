package com.thoughtworks.blackhorse.reporter.interfaces

import com.thoughtworks.blackhorse.schema.performance.Performance
import com.thoughtworks.blackhorse.schema.performance.TaskPerformance

object PerformanceColumns {
    val columns: List<FieldExtractor> = listOf(
        PerformanceExtractor("cardId", Performance::cardId),
        PerformanceExtractor("cardType", Performance::cardType),
        PerformanceExtractor("cardTitle", Performance::cardTitle),
        PerformanceExtractor("points", Performance::points),
        PerformanceExtractor("reporter", Performance::reporter),
        PerformanceExtractor("decompositionCost", Performance::decompositionCost),
        PerformanceExtractor("startAt", Performance::startAt),
        PerformanceExtractor("endAt", Performance::endAt),
        PerformanceExtractor("developer", Performance::developer),
        TaskExtractor("title", TaskPerformance::title),
        TaskExtractor("expectCost", TaskPerformance::expectCost),
        TaskExtractor("actualCost", TaskPerformance::actualCost),
        TaskExtractor("blockCost", TaskPerformance::blockCost),
        TaskExtractor("blockType", TaskPerformance::blockType),
        TaskExtractor("blockInstruction", TaskPerformance::blockInstruction),
        TaskExtractor("blockConsequence", TaskPerformance::blockConsequence),
    )
}
