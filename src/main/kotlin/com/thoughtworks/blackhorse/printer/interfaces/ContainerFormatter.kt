package com.thoughtworks.blackhorse.printer.interfaces

import com.thoughtworks.blackhorse.schema.architecture.Container

interface ContainerFormatter {
    fun container(container: Container): String
}
