package com.thoughtworks.blackhorse.schema.performance

import com.thoughtworks.blackhorse.schema.story.Flow
import com.thoughtworks.blackhorse.schema.story.FlowProcess
import java.lang.IllegalArgumentException

class FlowPerformanceBuilder(
    private val flow: Flow
) {
    val processes = mutableListOf<ProcessPerformanceBuilder>()

    fun process(name: String? = null): ProcessPerformanceBuilder {
        val process = name?.let { findProcessById(it) } ?: findNextProcess()
        return ProcessPerformanceBuilder(process).also(processes::add)
    }

    private fun findProcessById(name: String): FlowProcess {
        return flow.allProcesses.find { it.definition?.name == name }
            ?: throw IllegalArgumentException("No such process with name: $name")
    }

    private fun findNextProcess(): FlowProcess {
        if (processes.size == flow.allProcesses.size)
            throw IllegalArgumentException("You have complete all process, don't add invalid process")
        return flow.allProcesses[processes.size]
    }

    fun isFlowComplete() = processes.size == flow.allProcesses.size
    fun build() = processes.map(ProcessPerformanceBuilder::build)
}
