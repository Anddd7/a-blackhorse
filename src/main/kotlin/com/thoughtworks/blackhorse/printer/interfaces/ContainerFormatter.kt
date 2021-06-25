package com.thoughtworks.blackhorse.printer.interfaces

import com.thoughtworks.blackhorse.schema.architecture.Container

interface ContainerFormatter {
    fun anchors(items: Set<Container>): String
    fun containers(items: Set<Container>): String
}
