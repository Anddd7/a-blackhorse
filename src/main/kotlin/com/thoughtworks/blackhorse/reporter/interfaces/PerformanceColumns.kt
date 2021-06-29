package com.thoughtworks.blackhorse.reporter.interfaces

import com.thoughtworks.blackhorse.schema.performance.ProcessPerformance
import com.thoughtworks.blackhorse.schema.performance.StoryPerformance

object PerformanceColumns {
    val columns = listOf(
        StoryExtractor("cardId", StoryPerformance::cardId),
        StoryExtractor("cardType", StoryPerformance::cardType),
        StoryExtractor("cardTitle", StoryPerformance::cardTitle),
        StoryExtractor("points", StoryPerformance::points),
        StoryExtractor("reporter", StoryPerformance::reporter),
        StoryExtractor("decompositionCost", StoryPerformance::decompositionCost),
        StoryExtractor("startAt", StoryPerformance::startAt),
        StoryExtractor("endAt", StoryPerformance::endAt),
        StoryExtractor("developer", StoryPerformance::developer),
        ProcessExtractor("process", ProcessPerformance::processName),
        ProcessExtractor("expectCost", ProcessPerformance::expectCost),
        ProcessExtractor("actualCost", ProcessPerformance::actualCost),
        ProcessExtractor("blockCost", ProcessPerformance::blockCost),
        ProcessExtractor("blockType", ProcessPerformance::blockType),
        ProcessExtractor("blockInstruction", ProcessPerformance::blockInstruction),
        ProcessExtractor("blockConsequence", ProcessPerformance::blockConsequence),
    )
}
