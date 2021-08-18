package com.thoughtworks.blackhorse.schema.architecture

import com.thoughtworks.blackhorse.schema.architecture.attributes.TestDouble
import com.thoughtworks.blackhorse.schema.story.attributes.Complexity

/**
 * definition of a process (工序)
 */
data class ProcessDef(
    val name: String,
    val component: Component,
    val dependency: Component,
    val testDouble: TestDouble,
    val complexity: Complexity,
    val description: String,
    var testFramework: String?,
) {
    fun isIntra() = component.getContainer() == dependency.getContainer()
}

data class ProcessDefBuilder(
    val component: Component,
    val dependency: Component,
    val testDouble: TestDouble,
) {
    var description: String? = null
    var complexity: Complexity? = null
    var testFramework: String? = null

    fun build(containerId: String, serial: Int) =
        ProcessDef(
            "$containerId-$serial",
            component,
            dependency,
            testDouble,
            complexity ?: Complexity(0),
            description ?: "",
            testFramework,
        )
}

infix fun ProcessDefBuilder.cost(input: Int) = apply { complexity = Complexity(input) }
infix fun ProcessDefBuilder.at(fn: () -> String) = apply { description = fn() }
infix fun ProcessDefBuilder.with(input: String) = apply { testFramework = input }
