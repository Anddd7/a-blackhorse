package com.thoughtworks.blackhorse.schema.architecture

import com.thoughtworks.blackhorse.utils.extractProjectName

/**
 * the interface to define a single container
 */
interface Container : Node {
    val id: String
    val definitions: List<ProcessDefinitionBuilder>

    fun findProcessDefs(component: Component, dependency: Component): ProcessDefinition? {
        for ((index, builder) in definitions.withIndex()) {
            if (builder.component == component && builder.dependency == dependency)
                return builder.build(id, index + 1)
        }
        return null
    }

    fun getAllProcessDefs() =
        definitions.mapIndexed { index, builder ->
            builder.build(id, index + 1)
        }

    fun getProjectName(): String = javaClass.canonicalName.extractProjectName()

    private fun Component.createProcess(to: Component, testDouble: TestDouble) =
        ProcessDefinitionBuilder(this, to, testDouble)

    // define a process(工序)
    infix fun Component.call(other: Component) = createProcess(other, TestDouble.Real)
    infix fun Component.mock(other: Component) = createProcess(other, TestDouble.Mock)
    infix fun Component.fake(other: Component) = createProcess(other, TestDouble.Fake)
    infix fun Component.stub(other: Component) = createProcess(other, TestDouble.Stub)
    infix fun Component.dummy(other: Component) = createProcess(other, TestDouble.Dummy)
    infix fun Component.spy(other: Component) = createProcess(other, TestDouble.Spy)
}
