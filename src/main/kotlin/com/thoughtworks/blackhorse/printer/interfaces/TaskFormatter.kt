package com.thoughtworks.blackhorse.printer.interfaces

import com.thoughtworks.blackhorse.schema.story.Task

interface TaskFormatter {
    fun task(task: Task): String
    fun diagram(task: Task): String
}
