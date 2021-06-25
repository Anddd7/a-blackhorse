package com.thoughtworks.blackhorse.automation

import com.thoughtworks.blackhorse.config.ProjectConfig
import com.thoughtworks.blackhorse.config.StoryConfig
import com.thoughtworks.blackhorse.schema.architecture.Container
import com.thoughtworks.blackhorse.schema.architecture.getProjectName
import com.thoughtworks.blackhorse.schema.story.StoryOf
import com.thoughtworks.blackhorse.utils.findStories
import com.thoughtworks.blackhorse.utils.getContainerOrNull

object AutoBuildArchitectures {
    fun build(files: List<String>): Boolean {
        val containers = filterContainers(files).ifEmpty {
            logNoChanges()
            return false
        }

        logBeforeRebuild()
        containers.forEach { (projectName, containers) ->
            rebuildProject(projectName, containers.toSet())
        }
        logAfterRebuild()
        return true
    }

    private fun logAfterRebuild() {
        println(
            """
            | --> Done Automation Build Architecture
            -------------------------------------------------------
            """.trimIndent()
        )
    }

    private fun logBeforeRebuild() {
        println(
            """
            -------------------------------------------------------
            | --> Start Automation Build Architecture: Rebuild all related stories
            """.trimIndent()
        )
    }

    private fun logNoChanges() {
        println(
            """
            -------------------------------------------------------
            | Automation Build: No Architecture Changes
            -------------------------------------------------------
            """.trimIndent()
        )
    }

    private fun rebuildProject(projectName: String, containers: Set<Container>) {
        if (containers.isEmpty()) return

        ProjectConfig.execute(projectName) {
            findStories(projectName)
                .map(StoryOf::buildStory)
                .filter { it.containers.anyExistsIn(containers) }
                .forEach {
                    StoryConfig.execute(it.name) {
                        ProjectConfig.printer().story(it)
                    }
                }
        }
    }

    private fun Set<Container>.anyExistsIn(other: Set<Container>) =
        any { it in other }

    private fun filterContainers(files: List<String>) =
        files.mapNotNull(::getContainerOrNull).groupBy { it.getProjectName() }
}
