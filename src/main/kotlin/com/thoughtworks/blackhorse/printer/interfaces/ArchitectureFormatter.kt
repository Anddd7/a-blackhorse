package com.thoughtworks.blackhorse.printer.interfaces

import com.thoughtworks.blackhorse.schema.architecture.Architecture

interface ArchitectureFormatter {
    fun architecture(architecture: Architecture): String
}
