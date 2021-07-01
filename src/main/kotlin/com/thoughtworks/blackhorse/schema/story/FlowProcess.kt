package com.thoughtworks.blackhorse.schema.story

import com.thoughtworks.blackhorse.schema.architecture.Component
import com.thoughtworks.blackhorse.schema.story.attributes.ApiScenario
import java.util.concurrent.atomic.AtomicInteger

data class FlowProcess(
    val id: String,
    val start: Component,
    val target: Component,
    val accept: String? = null,
    val reply: String? = null,
    val targetApiScenario: ApiScenario? = null,
    val nested: List<FlowProcess> = emptyList(),
) {
    val startName = start.name()
    val targetName = target.name()
    val container = start.containerOf()
    val definition = container.processDefinition(start, target)

    fun components(): Set<Component> =
        LinkedHashSet<Component>().apply {
            add(start)
            add(target)
            nested.map { addAll(it.components()) }
        }

    fun processIterator(): List<FlowProcess> = listOf(this) + nested.flatMap { it.processIterator() }
}

data class FlowProcessBuilder(
    val start: Component,
    val target: Component,
) {
    var accept: String? = null
    var reply: String? = null
    var targetApiScenario: ApiScenario? = null
    var nested: MutableList<FlowProcessBuilder> = mutableListOf()

    // fork nested process
    infix fun Component.accept(string: String) = toNext(this) accept string
    infix fun Component.accept(fn: () -> String) = toNext(this) accept fn
    infix fun Component.reply(string: String) = toNext(this) reply string
    infix fun Component.reply(fn: () -> String) = toNext(this) reply fn
    infix fun Component.withApi(api: ApiScenario) = toNext(this) withApi api
    private fun toNext(next: Component) = FlowProcessBuilder(target, next).also(nested::add)

    fun build(flowId: String, idGenerator: AtomicInteger): FlowProcess {
        val processId = flowId + "-" + idGenerator.getAndIncrement()

        return FlowProcess(
            processId,
            start, target, accept, reply, targetApiScenario, nested.map { it.build(flowId, idGenerator) }
        )
    }
}

// define process
infix fun FlowProcessBuilder.accept(string: String) = apply { accept = string }
infix fun FlowProcessBuilder.accept(fn: () -> String) = apply { accept = fn() }
infix fun FlowProcessBuilder.reply(string: String) = apply { reply = string }
infix fun FlowProcessBuilder.reply(fn: () -> String) = apply { reply = fn() }
infix fun FlowProcessBuilder.withApi(api: ApiScenario) = apply { targetApiScenario = api }

// lambda util
infix fun FlowProcessBuilder.then(fn: FlowProcessBuilder.() -> Unit) = fn()
