package com.thoughtworks.blackhorse.schema.story

import com.thoughtworks.blackhorse.config.StoryContext
import com.thoughtworks.blackhorse.config.StoryContextHolder
import com.thoughtworks.blackhorse.printer.PrinterOption
import com.thoughtworks.blackhorse.schema.performance.CardType
import com.thoughtworks.blackhorse.schema.performance.StoryPerformance
import com.thoughtworks.blackhorse.schema.performance.StoryPerformanceBuilder
import com.thoughtworks.blackhorse.utils.extractProjectName
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.slf4j.Logger
import org.slf4j.LoggerFactory

open class StoryOf(
    val title: String,
    val estimation: Estimation,
    private val cardId: String? = null,
    val cardType: CardType = CardType.STORY,
    val configure: StoryBuilder.() -> Unit,
    val tracking: StoryPerformanceBuilder.() -> Unit = {},
) {
    private val log = LoggerFactory.getLogger(this.javaClass)

    private fun getCardId(): String = cardId ?: getStoryName()
    fun getStoryName(): String = javaClass.simpleName
    fun getProjectName(): String = javaClass.canonicalName.extractProjectName()

    fun buildStory(): Story =
        StoryBuilder(getStoryName(), getProjectName(), title, getCardId(), cardType, estimation.value)
            .apply(configure)
            .build()

    fun buildPerformance(): StoryPerformance? = runCatching {
        buildStory()
            .let(::StoryPerformanceBuilder)
            .apply(tracking)
            .build()
    }
        .onFailure { log.warn("Performance is not ready at ${getCardId()}, please update it as soon as possible!") }
        .getOrNull()

    fun printToJira() = print(PrinterOption.JIRA_ATTACHMENT)
    fun print(printer: PrinterOption? = null) {
        log.infoTime("Single") {
            runBlocking {
                val context = StoryContext.load(getProjectName(), getStoryName()).override(printer)

                StoryContextHolder.set(context)
                launch(StoryContextHolder.asContextElement()) {
                    StoryContextHolder.printer().print(buildStory())
                }
            }
        }
    }

    override fun toString(): String = getCardId()
}

fun Logger.infoTime(mode: String, fn: () -> Unit) {
    val startTime = System.currentTimeMillis()
    info("*******************************************************")
    info("🍖 Start Printing ...")
    info("✈️ Execution Mode: ☑️ $mode")
    fn()
    info("🍻 Finished, cost [${System.currentTimeMillis() - startTime}] ms")
    info("*******************************************************")
}
