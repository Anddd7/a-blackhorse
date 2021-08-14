package com.thoughtworks.projects.order

import com.thoughtworks.blackhorse.schema.story.attributes.Tag

enum class ProjectTags : Tag {
    ORDER;

    override fun value() = name
}
