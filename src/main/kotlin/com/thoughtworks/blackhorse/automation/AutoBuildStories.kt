package com.thoughtworks.blackhorse.automation

import com.thoughtworks.blackhorse.config.ProjectConfig
import com.thoughtworks.blackhorse.schema.story.StoryOf
import com.thoughtworks.blackhorse.schema.story.outputTo
import com.thoughtworks.blackhorse.utils.getStoryOrNull

object AutoBuildStories {
    fun build(files: List<String>): Boolean {
        val stories = filterStories(files).ifEmpty {
            logNoChanges()
            return false
        }
        logBeforeRebuild()
        rebuildProjects(stories)
        logAfterRebuild()
        return true
    }

    private fun logAfterRebuild() {
        println(
            """
            | --> Done Automation Build Stories
            -------------------------------------------------------
            """.trimIndent()
        )
    }

    private fun logBeforeRebuild() {
        println(
            """
            -------------------------------------------------------
            | --> Start Automation Build Stories
            """.trimIndent()
        )
    }

    private fun logNoChanges() {
        println(
            """
            -------------------------------------------------------
            | Automation Build: No Story Changes
            -------------------------------------------------------
            """.trimIndent()
        )
    }

    private fun rebuildProjects(projects: Map<String, List<StoryOf>>) {
        projects.forEach { (projectName, stories) ->
            rebuildStories(projectName, stories)
        }
    }

    private fun rebuildStories(projectName: String, stories: List<StoryOf>) {
        if (stories.isEmpty()) return

        ProjectConfig.execute(projectName) {
            stories.forEach {
                it.outputTo(ProjectConfig.printer())
            }
        }
    }

    private fun filterStories(files: List<String>) =
        files.mapNotNull(::getStoryOrNull).groupBy(StoryOf::getProjectName)
}
