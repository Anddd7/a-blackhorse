package com.thoughtworks.blackhorse.schema.architecture

import com.thoughtworks.blackhorse.schema.architecture.attributes.ComponentLayer
import com.thoughtworks.blackhorse.schema.architecture.attributes.TechStack
import com.thoughtworks.blackhorse.utils.getContainerOrNull

/**
 * the interface to define a functional component/layer in a process/container
 */
interface Component : Node {
    val techStack: List<TechStack>
    val responsibility: String
    val layer: ComponentLayer

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
}
