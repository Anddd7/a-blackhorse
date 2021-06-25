package com.thoughtworks.blackhorse.printer.interfaces

import com.thoughtworks.blackhorse.schema.story.Story

interface TableOfContentFormatter {
    fun toc(story: Story): String
}
