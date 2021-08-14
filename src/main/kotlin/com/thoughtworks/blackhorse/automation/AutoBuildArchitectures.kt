package com.thoughtworks.blackhorse.automation

import com.thoughtworks.blackhorse.config.StoryContext
import com.thoughtworks.blackhorse.config.StoryContextHolder
import com.thoughtworks.blackhorse.schema.architecture.Container
import com.thoughtworks.blackhorse.schema.story.StoryOf
import com.thoughtworks.blackhorse.utils.findStories
import com.thoughtworks.blackhorse.utils.getContainerOrNull
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import org.slf4j.Logger
import org.slf4j.LoggerFactory

object AutoBuildArchitectures {
    private val log: Logger = LoggerFactory.getLogger(this.javaClass)

    fun build(files: List<String>): Boolean {
        val containers = filterContainers(files).ifEmpty {
            logNoChanges()
            return false
        }

        logBeforeRebuild()
        runBlocking {
            withContext(Dispatchers.IO) {
                containers.flatMap { (projectName, containers) ->
                    rebuildProject(projectName, containers.toSet())
                }.awaitAll()
            }
        }
        logAfterRebuild()
        return true
    }

    private fun logAfterRebuild() {
        log.info("-------------------------------------------------------")
        log.info("| --> Done Automation Build Architecture")
        log.info("-------------------------------------------------------")
    }

    private fun logBeforeRebuild() {
        log.info("-------------------------------------------------------")
        log.info("| --> Start Automation Build Architecture: Rebuild all related stories")
        log.info("-------------------------------------------------------")
    }

    private fun logNoChanges() {
        log.info("-------------------------------------------------------")
        log.info("| Automation Build: No Architecture Changes")
        log.info("-------------------------------------------------------")
    }

    private suspend fun rebuildProject(projectName: String, containers: Set<Container>) = coroutineScope {
        findStories(projectName)
            .map(StoryOf::buildStory)
            .filter { it.containers.anyExistsIn(containers) }
            .map {
                val context = StoryContext.load(it.project, it.name)
                StoryContextHolder.set(context)
                async(coroutineContext + StoryContextHolder.asContextElement()) {
                    StoryContextHolder.printer().print(it)
                }
            }
    }

    private fun Set<Container>.anyExistsIn(other: Set<Container>) =
        any { it in other }

    private fun filterContainers(files: List<String>) =
        files.mapNotNull(::getContainerOrNull).groupBy { it.getProjectName() }
}
