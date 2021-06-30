package com.thoughtworks.blackhorse.schema.architecture

import com.thoughtworks.blackhorse.schema.story.Complexity

data class ProcessDefinition(
    val name: String,
    val component: Component,
    val dependency: Component,
    val testDouble: TestDouble,
    val complexity: Complexity,
    val description: String,
)

data class ProcessDefinitionBuilder(
    val component: Component,
    val dependency: Component,
    val testDouble: TestDouble,
) {
    var description: String? = null
    var complexity: Complexity? = null

    fun build(containerId: String, serial: Int) =
        ProcessDefinition(
            "$containerId-$serial",
            component,
            dependency,
            testDouble,
            complexity ?: Complexity.NONE,
            description ?: ""
        )
}

infix fun ProcessDefinitionBuilder.cost(input: Complexity) = apply { complexity = input }
infix fun ProcessDefinitionBuilder.cost(input: Int) = apply { complexity = Complexity.of(input) }
infix fun ProcessDefinitionBuilder.at(fn: () -> String) = apply { description = fn() }
