package com.thoughtworks.blackhorse.schema.story

import com.thoughtworks.blackhorse.config.StoryContext
import com.thoughtworks.blackhorse.config.StoryContextHolder
import com.thoughtworks.blackhorse.printer.PrinterOption
import com.thoughtworks.blackhorse.schema.performance.StoryPerformance
import com.thoughtworks.blackhorse.schema.performance.StoryPerformanceBuilder
import com.thoughtworks.blackhorse.schema.performance.attributes.CardType
import com.thoughtworks.blackhorse.schema.story.attributes.Estimation
import com.thoughtworks.blackhorse.schema.story.attributes.Tag
import com.thoughtworks.blackhorse.utils.extractProjectName
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.slf4j.Logger
import org.slf4j.LoggerFactory

open class StoryOf(
    val title: String,
    val estimation: Int,
    private val cardId: String? = null,
    val cardType: CardType = CardType.STORY,
    val configure: StoryBuilder.() -> Unit,
    val tracking: StoryPerformanceBuilder.() -> Unit = {},
    val tags: List<Tag> = emptyList(),
) {

    private val log = LoggerFactory.getLogger(this.javaClass)

    private fun getCardId(): String = cardId ?: getStoryName()
    fun getStoryName(): String = javaClass.simpleName
    fun getProjectName(): String = javaClass.canonicalName.extractProjectName()

    fun buildStory(): Story =
        StoryBuilder(
            getStoryName(),
            getProjectName(),
            title,
            getCardId(),
            cardType,
            Estimation.valueOf(estimation),
            tags.map(Tag::value)
        ).apply(configure).build()

    fun buildPerformance(): StoryPerformance? = runCatching {
        buildStory()
            .let(::StoryPerformanceBuilder)
            .apply(tracking)
            .build()
    }
        .onFailure { log.warn("Performance is not ready at ${getCardId()}, please update it as soon as possible!") }
        .getOrNull()

    fun printToPdf() = print(PrinterOption.PDF_PLANTUML)
    fun printToJira() = print(PrinterOption.JIRA_ATTACHMENT)
    fun print(printer: PrinterOption? = null) {
        log.infoTime("Single") {
            runBlocking {
                val context = StoryContext.load(getProjectName(), getStoryName()).override(printer)

                StoryContextHolder.set(context)
                launch(StoryContextHolder.asContextElement()) {
                    StoryContextHolder.storyPrinter().print(buildStory())
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
