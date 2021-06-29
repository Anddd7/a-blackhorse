package com.thoughtworks.blackhorse.schema.story

import com.thoughtworks.blackhorse.schema.architecture.Component
import java.util.concurrent.atomic.AtomicInteger

data class Flow(
    val id: String,
    val purpose: String,
    val processes: List<FlowProcess>,
) {
    val allProcesses = processes.flatMap(FlowProcess::processIterator)
}

class FlowBuilder(
    private val purpose: String,
) {
    private val processes = mutableListOf<FlowProcessBuilder>()

    // communicate with others
    infix fun Component.call(target: Component) = FlowProcessBuilder(this, target).also(processes::add)

    // self call
    infix fun Component.accept(string: String) = call(this) accept string
    infix fun Component.accept(fn: () -> String) = call(this) accept fn
    infix fun Component.reply(string: String) = call(this) reply string
    infix fun Component.reply(fn: () -> String) = call(this) reply fn
    infix fun Component.withApi(api: ApiScenario) = call(this) withApi api

    fun build(id: String): Flow {
        val increment = AtomicInteger(1)

        return Flow(id, purpose, processes.mapIndexed { _, item -> item.build(id, increment) })
    }
}
