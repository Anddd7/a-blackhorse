package com.thoughtworks.blackhorse.printer.interfaces

import com.thoughtworks.blackhorse.schema.architecture.Container
import com.thoughtworks.blackhorse.schema.architecture.attributes.ContainerLayer

interface ArchitecturePrinter {
    fun start(containers: Map<ContainerLayer, Container>)
}
