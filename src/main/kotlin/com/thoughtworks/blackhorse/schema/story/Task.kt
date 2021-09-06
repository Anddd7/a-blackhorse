package com.thoughtworks.blackhorse.schema.story

import com.thoughtworks.blackhorse.schema.architecture.Component
import com.thoughtworks.blackhorse.schema.story.attributes.ApiScenario
import java.util.concurrent.atomic.AtomicInteger

data class Task(
    val id: String,
    val start: Component,
    val target: Component,
    val accept: String? = null,
    val reply: String? = null,
    val targetApiScenario: ApiScenario? = null,
    val nested: List<Task> = emptyList(),
) {
    val startName = start.name()
    val targetName = target.name()
    val container = start.getContainer()
    val process = container.findProcess(start, target)

    fun components(): Set<Component> =
        LinkedHashSet<Component>().apply {
            add(start)
            add(target)
            nested.map { addAll(it.components()) }
        }

    fun getAllTasks(): List<Task> = listOf(this) + nested.flatMap { it.getAllTasks() }
}

data class TaskBuilder(
    val start: Component,
    val target: Component,
) {
    var accept: String? = null
    var reply: String? = null
    var targetApiScenario: ApiScenario? = null
    var nested: MutableList<TaskBuilder> = mutableListOf()

    // fork nested process
    // infix fun Component.accept(string: String) = toNext(this) accept string
    // infix fun Component.accept(fn: () -> String) = toNext(this) accept fn
    // infix fun Component.reply(string: String) = toNext(this) reply string
    // infix fun Component.reply(fn: () -> String) = toNext(this) reply fn
    // infix fun Component.withApi(api: ApiScenario) = toNext(this) withApi api
    // private fun toNext(next: Component) = TaskBuilder(target, next).also(nested::add)

    fun build(flowId: String, idGenerator: AtomicInteger): Task {
        val processId = flowId + "-" + idGenerator.getAndIncrement()

        return Task(
            processId,
            start,
            target,
            accept?.trimIndent(),
            reply?.trimIndent(),
            targetApiScenario,
            nested.map { it.build(flowId, idGenerator) }
        )
    }

    infix fun Component.call(target: Component) = TaskBuilder(this, target).also(nested::add)
}

// define process
infix fun TaskBuilder.given(string: String) = apply { accept = string }
infix fun TaskBuilder.given(fn: () -> String) = apply { accept = fn() }
infix fun TaskBuilder.withApi(api: ApiScenario) = apply { targetApiScenario = api }
// lambda util
infix fun TaskBuilder.nested(fn: TaskBuilder.() -> Unit) = apply { fn() }

@Deprecated("Use given to keep message order", replaceWith = ReplaceWith("given"))
infix fun TaskBuilder.expect(string: String) = apply { reply = string }

@Deprecated("Use given to keep message order", replaceWith = ReplaceWith("given"))
infix fun TaskBuilder.expect(fn: () -> String) = apply { reply = fn() }
