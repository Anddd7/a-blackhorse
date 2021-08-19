package com.thoughtworks.blackhorse.schema.story

import com.thoughtworks.blackhorse.schema.architecture.Container
import com.thoughtworks.blackhorse.schema.performance.attributes.CardType
import com.thoughtworks.blackhorse.schema.story.attributes.APISchema
import com.thoughtworks.blackhorse.schema.story.attributes.ApiScenario
import com.thoughtworks.blackhorse.schema.story.attributes.Estimation

data class Story(
    val name: String,
    val project: String,
    val title: String,
    val cardId: String,
    val inScope: String?,
    val outOfScope: String?,
    val acceptanceCriteria: List<AcceptanceCriteria>,
    val apis: List<APISchema>,
    val containers: Set<Container>,
    val cardType: CardType,
    val estimation: Estimation,
    val tags: List<String>,
)

class StoryBuilder(
    private val name: String,
    private val project: String,
    private val title: String,
    private val cardId: String,
    private val cardType: CardType,
    private val estimation: Estimation,
    private val tags: List<String>,
) {
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

    fun build(): Story {
        val craftedAcs = acceptanceCriteria.mapIndexed { index, item -> item.build((index + 1).toString()) }
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
            project,
            title,
            cardId,
            inScope,
            outOfScope,
            craftedAcs,
            craftedApis,
            craftedContainers,
            cardType,
            estimation,
            tags,
        )
    }
}
