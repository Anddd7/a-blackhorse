package com.thoughtworks.blackhorse.printer.interfaces

import com.thoughtworks.blackhorse.schema.story.Flow

interface FlowFormatter {
    fun anchors(items: List<Flow>): String
    fun flows(items: List<Flow>): String
}
