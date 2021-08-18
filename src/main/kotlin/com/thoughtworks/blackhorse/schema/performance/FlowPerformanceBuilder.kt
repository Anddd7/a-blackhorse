package com.thoughtworks.blackhorse.schema.performance

import com.thoughtworks.blackhorse.schema.story.Flow
import com.thoughtworks.blackhorse.utils.findByKeyOrNext

class FlowPerformanceBuilder(
    private val flow: Flow
) {
    private val processes = mutableListOf<ProcessPerformanceBuilder>()

    fun process(name: String? = null): ProcessPerformanceBuilder {
        val process = flow.allTasks.findByKeyOrNext({ it.process?.name }, name, processes.size)
        return ProcessPerformanceBuilder(process).also(processes::add)
    }

    fun build() = processes.map { it.build(flow.id) }
}
