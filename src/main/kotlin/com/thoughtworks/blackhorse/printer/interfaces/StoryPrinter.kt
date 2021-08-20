package com.thoughtworks.blackhorse.printer.interfaces

import com.thoughtworks.blackhorse.config.StoryContextHolder
import com.thoughtworks.blackhorse.schema.story.Story
import org.slf4j.LoggerFactory

private val log = LoggerFactory.getLogger(StoryPrinter::class.java)

interface StoryPrinter {
    fun start(story: Story)
    fun print(story: Story) {
        log.info("🪁 Printing story [${story.name}] in [${story.project}] with ${this.javaClass.simpleName}")
        StoryContextHolder.clearUp()
        start(story)
        log.info("🪁 **** 🆗 Done story [${story.name}]")
    }
}
