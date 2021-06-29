package com.thoughtworks.blackhorse.schema.story

import com.thoughtworks.blackhorse.config.ProjectConfig
import com.thoughtworks.blackhorse.config.StoryConfig
import com.thoughtworks.blackhorse.printer.PrinterOption
import com.thoughtworks.blackhorse.printer.interfaces.StoryPrinter
import com.thoughtworks.blackhorse.schema.performance.CardType
import com.thoughtworks.blackhorse.schema.performance.StoryPerformanceBuilder
import com.thoughtworks.blackhorse.utils.extractProjectName

open class StoryOf(
    val title: String,
    val estimation: Estimation,
    private val cardId: String? = null,
    val cardType: CardType = CardType.STORY,
    val configure: StoryBuilder.() -> Unit,
    val tracking: StoryPerformanceBuilder.() -> Unit = {},
) {
    fun getName(): String = javaClass.simpleName
    fun getCardId(): String = cardId ?: getName()
    fun getProjectName(): String = javaClass.canonicalName.extractProjectName()

    fun buildStory() = StoryBuilder(getName(), title, getCardId(), cardType, estimation.value).apply(configure).build()
    fun buildPerformance() = StoryPerformanceBuilder(buildStory()).apply(tracking).build()

    override fun toString(): String = getCardId()
}

fun StoryOf.preview() = ProjectConfig.execute(getProjectName()) {
    StoryConfig.execute(getName()) {
        ProjectConfig.printer().preview(buildStory())
    }
}

fun StoryOf.print(printer: StoryPrinter? = null) = ProjectConfig.execute(getProjectName()) {
    outputTo(printer ?: ProjectConfig.printer())
}

fun StoryOf.printToJira() = print(PrinterOption.JIRA_ATTACHMENT.printer)

fun StoryOf.outputTo(printer: StoryPrinter) = StoryConfig.execute(getName()) {
    printer.story(buildStory())
}

fun StoryOf.evaluate() =
    runCatching(StoryOf::buildPerformance)
        .onFailure { println("Performance is not ready at ${getCardId()}, please update it as soon as possible!") }
        .getOrNull()
