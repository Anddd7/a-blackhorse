package com.thoughtworks.blackhorse.reporter.interfaces

import com.thoughtworks.blackhorse.schema.performance.Performance
import com.thoughtworks.blackhorse.schema.performance.TaskPerformance

interface FieldExtractor {
    val name: String
    fun extract(performance: Performance, index: Int): String
}

data class PerformanceExtractor(
    override val name: String,
    private val get: (Performance) -> Any?
) : FieldExtractor {
    override fun extract(performance: Performance, index: Int) = performance.takeIf { index == 0 }.getOrEmpty(get)
}

data class TaskExtractor(
    override val name: String,
    private val get: (TaskPerformance) -> Any?
) : FieldExtractor {
    override fun extract(performance: Performance, index: Int) = extract(performance.tasks[index])
    private fun extract(task: TaskPerformance) = task.getOrEmpty(get)
}

private fun <T> T?.getOrEmpty(fn: (T) -> Any?) = this?.run(fn)?.toString().orEmpty()
