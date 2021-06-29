package com.thoughtworks.blackhorse.schema.story

interface Complexity {
    val cost: Int
    fun label() = "$cost mins"

    companion object {
        fun of(cost: Int): Complexity = DynamicComplexity(cost)
    }

    private data class DynamicComplexity(override val cost: Int) : Complexity
}

enum class CommonComplexity(override val cost: Int) : Complexity {
    DONE_ALREADY(0),
    SMALL(30),
    MEDIUM(60),
    LARGE(120),
    HUGE(240),
}
