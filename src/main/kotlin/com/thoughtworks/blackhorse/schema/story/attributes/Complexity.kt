package com.thoughtworks.blackhorse.schema.story.attributes

data class Complexity(val cost: Int) {
    fun label() = "$cost mins"
}
