package com.thoughtworks.blackhorse.schema.story.attributes

/**
 * create project tags and attach to stories,
 * search (with idea) story would be easier
 */
interface Tag {
    // TODO output to markdown
    fun value(): String
}
