package com.thoughtworks.blackhorse.config

import kotlinx.coroutines.asContextElement

object StoryContextHolder {
    private val instance = ThreadLocal<StoryContext>()

    fun set(new: StoryContext) = instance.set(new)
    fun get() = instance.get() ?: throw IllegalAccessException("run StoryContext.load() first")

    fun asContextElement() = instance.asContextElement()
}
