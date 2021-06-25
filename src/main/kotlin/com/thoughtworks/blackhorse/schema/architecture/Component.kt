package com.thoughtworks.blackhorse.schema.architecture

import com.thoughtworks.blackhorse.utils.getContainerOrNull

// functional component/layer in a process/container
interface Component : Node {
    fun containerOf(): Container {
        val container = getContainerOrNull(javaClass.name.substringBeforeLast("$"))
        if (container == null) {
            println(
                """
                Please create the component object under a container object:

                object xxx : Container {
                    object xxx : Component
                }
                """.trimIndent()
            )
            throw IllegalArgumentException("No such container")
        }
        return container
    }
}
