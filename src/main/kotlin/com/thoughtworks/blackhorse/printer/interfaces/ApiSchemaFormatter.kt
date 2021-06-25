package com.thoughtworks.blackhorse.printer.interfaces

import com.thoughtworks.blackhorse.schema.story.APISchema

interface ApiSchemaFormatter {
    fun anchors(items: List<APISchema>): String
    fun schemas(items: List<APISchema>): String
}
