package com.thoughtworks.blackhorse.printer.interfaces

import com.thoughtworks.blackhorse.schema.story.Story

interface StoryFormatter {
    fun story(story: Story): String
    fun summary(story: Story): String
}
