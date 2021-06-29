package com.thoughtworks.blackhorse.reporter.interfaces

import com.thoughtworks.blackhorse.schema.performance.StoryPerformance
import com.thoughtworks.blackhorse.schema.performance.FlowPerformance

object PerformanceColumns {
    val columns: List<FieldExtractor> = listOf(
        StoryExtractor("cardId", StoryPerformance::cardId),
        StoryExtractor("cardType", StoryPerformance::cardType),
        StoryExtractor("cardTitle", StoryPerformance::cardTitle),
        StoryExtractor("points", StoryPerformance::points),
        StoryExtractor("reporter", StoryPerformance::reporter),
        StoryExtractor("decompositionCost", StoryPerformance::decompositionCost),
        StoryExtractor("startAt", StoryPerformance::startAt),
        StoryExtractor("endAt", StoryPerformance::endAt),
        StoryExtractor("developer", StoryPerformance::developer),
        FlowExtractor("task", FlowPerformance::flowId),
        FlowExtractor("expectCost", FlowPerformance::expectCost),
        FlowExtractor("actualCost", FlowPerformance::actualCost),
        FlowExtractor("blockCost", FlowPerformance::blockCost),
        FlowExtractor("blockType", FlowPerformance::blockType),
        FlowExtractor("blockInstruction", FlowPerformance::blockInstruction),
        FlowExtractor("blockConsequence", FlowPerformance::blockConsequence),
    )
}
