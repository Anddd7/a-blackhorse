package com.thoughtworks.blackhorse.schema.story.attributes

enum class Estimation(val value: Int) {
    ONE_1(1),
    TWO_2(2),
    THREE_3(3),
    FIVE_5(5),
    EIGHT_8(8),
    THIRTEEN_13(13),
    ;

    companion object {
        fun valueOf(value: Int) = values().find { it.value == value }
            ?: throw IllegalArgumentException("Please enter valid estimation points.")
    }
}
