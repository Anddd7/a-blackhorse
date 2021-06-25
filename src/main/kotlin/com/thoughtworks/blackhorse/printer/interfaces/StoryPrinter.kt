package com.thoughtworks.blackhorse.printer.interfaces

import com.thoughtworks.blackhorse.schema.story.Story

interface StoryPrinter {
    fun story(story: Story)
    fun preview(story: Story) = story(story)
}
