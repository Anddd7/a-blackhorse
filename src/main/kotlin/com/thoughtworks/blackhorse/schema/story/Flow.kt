package com.thoughtworks.blackhorse.schema.story

import com.thoughtworks.blackhorse.schema.architecture.Component

data class Flow(
    val purpose: String,
    val complexity: Complexity,
    val processes: List<FlowProcess>,
)

class FlowBuilder(
    private val purpose: String,
    private val complexity: Complexity,
) : Builder<Flow> {
    private val processes = mutableListOf<FlowProcessBuilder>()

    // communicate with others
    infix fun Component.call(target: Component) = FlowProcessBuilder(this, target).also(processes::add)

    // self call
    infix fun Component.accept(string: String) = call(this) accept string
    infix fun Component.accept(fn: () -> String) = call(this) accept fn
    infix fun Component.reply(string: String) = call(this) reply string
    infix fun Component.reply(fn: () -> String) = call(this) reply fn
    infix fun Component.withApi(api: ApiScenario) = call(this) withApi api

    override fun build() = Flow(purpose, complexity, processes.map(FlowProcessBuilder::build))
}
