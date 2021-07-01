package com.thoughtworks.blackhorse.schema.story.attributes

interface Complexity {
    val cost: Int
    fun label() = "$cost mins"

    companion object {
        fun of(cost: Int): Complexity = DynamicComplexity(cost)
        val NONE = of(0)
        val SMALL = of(30)
        val MEDIUM = of(60)
        val LARGE = of(120)

        @Deprecated("better to split this process")
        val HUGE = of(240)
    }

    private data class DynamicComplexity(override val cost: Int) : Complexity
}
