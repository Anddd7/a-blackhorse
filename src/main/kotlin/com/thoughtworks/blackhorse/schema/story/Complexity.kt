package com.thoughtworks.blackhorse.schema.story

enum class Complexity(val minutes: Int) {
    DONE_ALREADY(0),
    OUT_OF_SCOPE(0),
    SMALL(30),
    MEDIUM(60),
    LARGE(120),
    HUGE(240),
}
