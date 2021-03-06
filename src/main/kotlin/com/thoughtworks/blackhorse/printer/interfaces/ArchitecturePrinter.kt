package com.thoughtworks.blackhorse.printer.interfaces

import com.thoughtworks.blackhorse.schema.architecture.Architecture

interface ArchitecturePrinter {
    fun start(architecture: Architecture)
}
