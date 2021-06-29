package com.thoughtworks.blackhorse.reporter.interfaces

import com.thoughtworks.blackhorse.schema.performance.StoryPerformance
import com.thoughtworks.blackhorse.schema.performance.FlowPerformance

interface FieldExtractor {
    val name: String
    fun extract(performance: StoryPerformance, index: Int): String
}

data class StoryExtractor(
    override val name: String,
    private val get: (StoryPerformance) -> Any?
) : FieldExtractor {
    override fun extract(performance: StoryPerformance, index: Int) = performance.takeIf { index == 0 }.getOrEmpty(get)
}

data class FlowExtractor(
    override val name: String,
    private val get: (FlowPerformance) -> Any?
) : FieldExtractor {
    override fun extract(performance: StoryPerformance, index: Int) = extract(performance.flows[index])
    private fun extract(flow: FlowPerformance) = flow.getOrEmpty(get)
}

private fun <T> T?.getOrEmpty(fn: (T) -> Any?) = this?.run(fn)?.toString().orEmpty()
