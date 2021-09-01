package com.thoughtworks.blackhorse.config

enum class HiddenOption {
    COMPLEXITY;

    fun isVisible() = ProjectContextHolder.get().isVisible(this)
}
