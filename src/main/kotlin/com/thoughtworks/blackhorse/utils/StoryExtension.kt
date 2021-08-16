@file:Suppress("UnstableApiUsage")

package com.thoughtworks.blackhorse.utils

import com.google.common.reflect.ClassPath
import com.thoughtworks.blackhorse.reporter.PerformancePrinter
import com.thoughtworks.blackhorse.schema.architecture.Container
import com.thoughtworks.blackhorse.schema.story.StoryOf

// story extension
fun findStories(projectName: String) = ClassPath.from(PerformancePrinter.javaClass.classLoader)
    .getTopLevelClassesRecursive("com.thoughtworks.projects.$projectName")
    .mapNotNull {
        runCatching { it.load().kotlin.objectInstance as? StoryOf }.getOrNull()
    }

fun getStoryOrNull(classpath: String) =
    runCatching { Class.forName(classpath).kotlin.objectInstance as? StoryOf }.getOrNull()

// node extension
fun getContainerOrNull(classpath: String) =
    runCatching { Class.forName(classpath).kotlin.objectInstance as? Container }.getOrNull()

fun String.extractProjectName() = substringAfter("com.thoughtworks.projects.").substringBefore(".")

