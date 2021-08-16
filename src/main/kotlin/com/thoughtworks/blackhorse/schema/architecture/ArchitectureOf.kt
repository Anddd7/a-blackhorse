package com.thoughtworks.blackhorse.schema.architecture

import com.thoughtworks.blackhorse.config.ProjectContext
import com.thoughtworks.blackhorse.config.ProjectContextHolder
import com.thoughtworks.blackhorse.schema.story.infoTime
import com.thoughtworks.blackhorse.utils.extractProjectName
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.slf4j.LoggerFactory

data class Architecture(
    val projectName: String,
    val containers: Set<Container>,
    val version: String,
    val changelogs: String,
)

open class ArchitectureOf(
    private val version: String,
    private val changelogs: String,
    private vararg val containers: Container
) {
    private val log = LoggerFactory.getLogger(this.javaClass)

    private fun getProjectName(): String = javaClass.canonicalName.extractProjectName()

    fun print() {

        log.infoTime("Architecture") {
            runBlocking {
                val context = ProjectContext.load(getProjectName())

                ProjectContextHolder.set(context)
                launch(ProjectContextHolder.asContextElement()) {
                    ProjectContextHolder.architecturePrinter().start(build())
                }
            }
        }
    }

    private fun build() = Architecture(getProjectName(), containers.toSet(), version, changelogs)
}
