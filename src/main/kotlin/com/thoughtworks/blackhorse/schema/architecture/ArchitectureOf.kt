package com.thoughtworks.blackhorse.schema.architecture

import com.thoughtworks.blackhorse.config.ProjectContext
import com.thoughtworks.blackhorse.schema.story.infoTime
import com.thoughtworks.blackhorse.utils.extractProjectName
import org.slf4j.LoggerFactory

open class ArchitectureOf(
    vararg containers: Container
) {
    private val groups = containers.groupBy(Container::layer)

    private val log = LoggerFactory.getLogger(this.javaClass)

    fun getProjectName(): String = javaClass.canonicalName.extractProjectName()

    fun print() {
        log.infoTime("Architecture") {
            val context = ProjectContext.load(getProjectName())


        }
    }
}
