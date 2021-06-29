package com.thoughtworks.blackhorse.schema.architecture

import com.thoughtworks.blackhorse.schema.story.Complexity

data class ProcessDefinition(
    val name: String,
    val component: Component,
    val dependency: Component,
    val testDouble: TestDouble,
    val complexity: Int,
    val description: String,
)

data class ProcessDefinitionBuilder(
    val component: Component,
    val dependency: Component,
    val testDouble: TestDouble,
) {
    var description: String? = null
    var cost: Int? = null

    fun build(containerId: String, serial: Int) =
        ProcessDefinition("$containerId-$serial", component, dependency, testDouble, cost ?: 0, description ?: "")
}

infix fun ProcessDefinitionBuilder.cost(complexity: Complexity) = apply { cost = complexity.minutes }
infix fun ProcessDefinitionBuilder.cost(int: Int) = apply { cost = int }
infix fun ProcessDefinitionBuilder.at(fn: () -> String) = apply { description = fn() }
