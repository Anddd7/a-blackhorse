package com.thoughtworks.blackhorse.schema.story

import com.thoughtworks.blackhorse.schema.architecture.Container
import com.thoughtworks.blackhorse.schema.performance.CardType

data class Story(
    val name: String,
    val title: String,
    val cardId: String,
    val inScope: String?,
    val outOfScope: String?,
    val acceptanceCriteria: List<AcceptanceCriteria>,
    val apis: List<APISchema>,
    val containers: Set<Container>,
    val cardType: CardType,
    val estimation: Int,
)

interface Builder<T> {
    fun build(): T
}

class StoryBuilder(
    private val name: String,
    private val title: String,
    private val cardId: String,
    private val cardType: CardType,
    private val estimation: Int,
) : Builder<Story> {
    private var inScope: String? = null
    private var outOfScope: String? = null
    private val acceptanceCriteria = mutableListOf<AcceptanceCriteriaBuilder>()

    fun inScope(fn: () -> String) {
        inScope = fn().trimIndent()
    }

    fun outOfScope(fn: () -> String) {
        outOfScope = fn().trimIndent()
    }

    fun ac(configure: AcceptanceCriteriaBuilder.() -> Unit) =
        acceptanceCriteria.add(AcceptanceCriteriaBuilder().apply(configure))

    override fun build(): Story {
        val craftedAcs = acceptanceCriteria.map(AcceptanceCriteriaBuilder::build)
        val craftedApis = craftedAcs.flatMap(AcceptanceCriteria::apiScenarios)
            .groupBy(ApiScenario::api)
            .map { (key, values) ->
                APISchema(key, values.distinctBy(ApiScenario::statusCode).sortedBy(ApiScenario::statusCode))
            }
        val craftedContainers = LinkedHashSet<Container>().apply {
            craftedAcs.map { addAll(it.containers()) }
        }

        return Story(
            name,
            title,
            cardId,
            inScope,
            outOfScope,
            craftedAcs,
            craftedApis,
            craftedContainers,
            cardType,
            estimation
        )
    }
}
