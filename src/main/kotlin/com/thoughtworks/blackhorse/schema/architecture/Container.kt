package com.thoughtworks.blackhorse.schema.architecture

import com.thoughtworks.blackhorse.utils.extractProjectName

// single process, deployed as 1 container
interface Container : Node {
    val id: String
    val definitions: List<ProcessDefinitionBuilder>

    fun processDefinition(component: Component, dependency: Component): ProcessDefinition? {
        for ((index, builder) in definitions.withIndex()) {
            if (builder.component == component && builder.dependency == dependency)
                return builder.build(id, index + 1)
        }
        return null
    }

    fun processDefinitions() =
        definitions.mapIndexed { index, builder ->
            builder.build(id, index + 1)
        }

    infix fun Component.call(other: Component) = createProcess(other, TestDouble.Real)
    infix fun Component.mock(other: Component) = createProcess(other, TestDouble.Mock)
    infix fun Component.fake(other: Component) = createProcess(other, TestDouble.Fake)
    infix fun Component.stub(other: Component) = createProcess(other, TestDouble.Stub)
    infix fun Component.dummy(other: Component) = createProcess(other, TestDouble.Dummy)
    infix fun Component.spy(other: Component) = createProcess(other, TestDouble.Spy)

    private fun Component.createProcess(to: Component, testDouble: TestDouble) =
        ProcessDefinitionBuilder(this, to, testDouble)
}

fun Container.getProjectName(): String = javaClass.canonicalName.extractProjectName()
