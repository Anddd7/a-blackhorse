package com.thoughtworks.blackhorse.automation

import com.thoughtworks.blackhorse.config.StoryContext
import com.thoughtworks.blackhorse.config.StoryContextHolder
import com.thoughtworks.blackhorse.schema.story.StoryOf
import com.thoughtworks.blackhorse.utils.getStoryOrNull
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import org.slf4j.Logger
import org.slf4j.LoggerFactory

object AutoBuildStories {
    private val log: Logger = LoggerFactory.getLogger(this.javaClass)

    fun build(files: List<String>): Boolean {
        val stories = filterStories(files).ifEmpty {
            logNoChanges()
            return false
        }
        logBeforeRebuild()
        runBlocking {
            withContext(Dispatchers.IO) {
                stories.flatMap { (projectName, stories) ->
                    rebuildStories(projectName, stories)
                }.awaitAll()
            }
        }
        logAfterRebuild()
        return true
    }

    private fun logAfterRebuild() {
        log.info("-------------------------------------------------------")
        log.info("| --> Done Automation Build Stories")
        log.info("-------------------------------------------------------")
    }

    private fun logBeforeRebuild() {
        log.info("-------------------------------------------------------")
        log.info("| --> Start Automation Build Stories")
        log.info("-------------------------------------------------------")
    }

    private fun logNoChanges() {
        log.info("-------------------------------------------------------")
        log.info("| Automation Build: No Story Changes")
        log.info("-------------------------------------------------------")
    }

    private suspend fun rebuildStories(projectName: String, stories: List<StoryOf>) = coroutineScope {
        stories.map {
            val context = StoryContext.load(projectName, it.getStoryName())
            StoryContextHolder.set(context)
            async(coroutineContext + StoryContextHolder.asContextElement()) {
                StoryContextHolder.get().printer.print(it.buildStory())
            }
        }
    }

    private fun filterStories(files: List<String>) =
        files.mapNotNull(::getStoryOrNull).groupBy(StoryOf::getProjectName)
}
