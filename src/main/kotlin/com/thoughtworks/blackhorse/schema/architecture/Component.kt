package com.thoughtworks.blackhorse.schema.architecture

import com.thoughtworks.blackhorse.schema.architecture.attributes.ComponentLayer
import com.thoughtworks.blackhorse.schema.architecture.attributes.ContainerLayer
import com.thoughtworks.blackhorse.schema.architecture.attributes.TechStack
import com.thoughtworks.blackhorse.utils.getContainerOrNull

/**
 * the interface to define a functional component/layer in a process/container
 */
abstract class Component(
    val layer: ComponentLayer,
    val techStack: List<TechStack> = emptyList(),
    val responsibility: String = "",
) : Node {

    fun getContainer(): Container {
        val container = getContainerOrNull(javaClass.name.substringBeforeLast("$"))
        if (container == null) {
            log().error("Please create the component object under a container object:")
            log().error("   object xxx : Container {    ")
            log().error("       object xxx : Component  ")
            log().error("   }   ")
            throw IllegalArgumentException("No such container")
        }
        return container
    }

    fun simpleName() = javaClass.name.substringAfterLast("$")
}

// a dummy component represent unknown external source
object Ext : Container(
    id = "Dummy",
    layer = ContainerLayer("DUMMAY-EXTERNAL"),
) {
    object Unknown : Component(
        layer = ComponentLayer("DUMMAY-EXTERNAL"),
    )

    override fun getProcesses(): List<ProcessDefBuilder> = emptyList()
}
