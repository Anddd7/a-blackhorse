package com.thoughtworks.blackhorse.schema.architecture

import com.google.common.reflect.ClassPath
import com.thoughtworks.blackhorse.schema.architecture.attributes.ContainerLayer
import com.thoughtworks.blackhorse.schema.architecture.attributes.DomainLogic
import com.thoughtworks.blackhorse.schema.architecture.attributes.TechStack
import com.thoughtworks.blackhorse.schema.architecture.attributes.TestDouble
import com.thoughtworks.blackhorse.schema.performance.attributes.Member
import com.thoughtworks.blackhorse.utils.extractProjectName

/**
 * the interface to define a single container
 */
abstract class Container(
    val id: String,
    val layer: ContainerLayer,
    val techStack: List<TechStack> = emptyList(),
    val domains: List<DomainLogic> = emptyList(),
    val owner: List<Member> = emptyList(),
    val responsibility: String = "",
) : Node {
    abstract fun getDefinitions(): List<ProcessDefinitionBuilder>

    fun findProcessDefs(component: Component, dependency: Component): ProcessDefinition? {
        for ((index, builder) in getDefinitions().withIndex()) {
            if (builder.component == component && builder.dependency == dependency)
                return builder.build(id, index + 1)
        }
        return null
    }

    fun getAllProcessDefs() = getDefinitions().mapIndexed { index, builder ->
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

    @Suppress("UnstableApiUsage")
    fun getComponents() =
        ClassPath.from(this.javaClass.classLoader).allClasses
            .filter {
                it.name.startsWith(this.javaClass.name)
            }
            .mapNotNull {
                runCatching { it.load().kotlin.objectInstance as Component? }.getOrNull()
            }
}
