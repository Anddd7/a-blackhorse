package com.thoughtworks.blackhorse.reporter.interfaces

import com.thoughtworks.blackhorse.schema.performance.ProcessPerformance
import com.thoughtworks.blackhorse.schema.performance.StoryPerformance

interface FieldExtractor {
    val name: String
    fun extract(lineNo: Int, story: StoryPerformance, process: ProcessPerformance): String
}

data class StoryExtractor(
    override val name: String,
    private val get: (StoryPerformance) -> Any?
) : FieldExtractor {
    override fun extract(lineNo: Int, story: StoryPerformance, process: ProcessPerformance) =
        story.takeIf { lineNo == 0 }.getOrEmpty(get)
}

data class ProcessExtractor(
    override val name: String,
    private val get: (ProcessPerformance) -> Any?
) : FieldExtractor {
    override fun extract(lineNo: Int, story: StoryPerformance, process: ProcessPerformance) =
        process.getOrEmpty(get)
}

private fun <T> T?.getOrEmpty(fn: (T) -> Any?) = this?.run(fn)?.toString().orEmpty()
