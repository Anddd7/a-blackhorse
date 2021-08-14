package com.thoughtworks.projects.order.onboarding

import com.thoughtworks.blackhorse.schema.story.attributes.Tag

enum class ProjectTags : Tag {
    ORDER;

    override fun value() = name
}
