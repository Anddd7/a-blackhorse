package com.thoughtworks.blackhorse.schema.performance

import com.thoughtworks.blackhorse.schema.performance.attributes.CardType
import com.thoughtworks.blackhorse.schema.performance.attributes.Member
import com.thoughtworks.blackhorse.schema.story.AcceptanceCriteria
import com.thoughtworks.blackhorse.schema.story.Story
import com.thoughtworks.blackhorse.utils.findByKeyOrNext
import java.time.LocalDate

data class StoryPerformance(
    val cardId: String,
    val cardTitle: String,
    val cardType: CardType,
    val points: Int,
    val reporter: String,
    val decompositionCost: Int,
    val developer: String,
    val startAt: LocalDate,
    val endAt: LocalDate,
    val processes: List<ProcessPerformance>
)

class StoryPerformanceBuilder(
    val story: Story
) {
    private var reporter: String? = null
    private var decompositionCost: Int? = null
    private var developer: String? = null
    private var startAt: LocalDate? = null
    private var endAt: LocalDate? = null

    fun decomposition(reporter: String, decompositionCost: Int) {
        this.reporter = reporter
        this.decompositionCost = decompositionCost
    }

    fun decomposition(member: Member, decompositionCost: Int) {
        decomposition(member.name, decompositionCost)
    }

    private fun development(developer: String, startAt: String) {
        this.developer = developer
        this.startAt = LocalDate.parse(startAt)
    }

    fun development(developer: Member, startAt: String) {
        development(developer.name, startAt)
    }

    fun finish(endAt: String) {
        this.endAt = LocalDate.parse(endAt)
    }

    private val acs = mutableListOf<AcPerformanceBuilder>()

    fun ac(id: String? = null, fn: AcPerformanceBuilder.() -> Unit) {
        val ac = story.acceptanceCriteria.findByKeyOrNext(AcceptanceCriteria::id, id, acs.size)
        acs.add(AcPerformanceBuilder(ac).apply(fn))
    }

    fun build() = StoryPerformance(
        story.cardId,
        story.title,
        story.cardType,
        story.estimation,
        reporter ?: throw IllegalArgumentException("missing required data"),
        decompositionCost ?: throw IllegalArgumentException("missing required data"),
        developer ?: throw IllegalArgumentException("missing required data"),
        startAt ?: throw IllegalArgumentException("missing required data"),
        endAt ?: throw IllegalArgumentException("missing required data"),
        acs.flatMap(AcPerformanceBuilder::build),
    )
}
