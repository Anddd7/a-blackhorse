package com.thoughtworks.blackhorse.printer.interfaces

import com.thoughtworks.blackhorse.schema.story.FlowProcess

interface ProcessFormatter {
    fun process(process: FlowProcess): String
    fun diagram(process: FlowProcess): String
}
