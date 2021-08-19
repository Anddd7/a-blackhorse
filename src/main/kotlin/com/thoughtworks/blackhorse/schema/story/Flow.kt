package com.thoughtworks.blackhorse.schema.story

import com.thoughtworks.blackhorse.schema.architecture.Component
import com.thoughtworks.blackhorse.schema.architecture.Ext
import com.thoughtworks.blackhorse.schema.story.attributes.ApiScenario
import java.util.concurrent.atomic.AtomicInteger

data class Flow(
    val id: String,
    val example: String,
    val tasks: List<Task>,
) {
    val allTasks = tasks.flatMap(Task::getAllTasks)
}

class FlowBuilder(
    private val example: String,
) {
    private val tasks = mutableListOf<TaskBuilder>()

    // communicate with others
    infix fun Component.call(target: Component) = TaskBuilder(this, target).also(tasks::add)

    // self call
    infix fun Component.accept(string: String) = Ext.Unknown.call(this) given string
    infix fun Component.accept(fn: () -> String) = Ext.Unknown.call(this) given fn
    infix fun Component.reply(string: String) = Ext.Unknown.call(this) expect string
    infix fun Component.reply(fn: () -> String) = Ext.Unknown.call(this) expect fn
    infix fun Component.withApi(api: ApiScenario) = Ext.Unknown.call(this) withApi api

    fun build(id: String): Flow {
        val increment = AtomicInteger(1)

        return Flow(id, example, tasks.mapIndexed { _, item -> item.build(id, increment) })
    }
}
