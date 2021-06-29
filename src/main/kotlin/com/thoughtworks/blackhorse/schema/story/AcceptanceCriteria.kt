package com.thoughtworks.blackhorse.schema.story

data class AcceptanceCriteria(
    val id: String,
    val description: String,
    var example: String?,
    var mockup: List<String>,
    var link: Map<String, String>,
    var note: String?,
    val flows: List<Flow>,
) {
    internal fun apiScenarios() = flows.flatMap(Flow::processes).mapNotNull(FlowProcess::targetApiScenario)
    internal fun containers() = flows.flatMap(Flow::processes).map(FlowProcess::container)
}

class AcceptanceCriteriaBuilder {
    private var description: String? = null
    private var example: String? = null
    private var mockup = mutableListOf<String>()
    private var link = mutableMapOf<String, String>()
    private var note: String? = null
    private var flows = mutableListOf<FlowBuilder>()

    fun description(fn: () -> String) {
        description = fn().trimIndent()
    }

    fun example(fn: () -> String) {
        example = fn().trimIndent()
    }

    fun mockup(url: String) {
        mockup.add(url)
    }

    fun link(title: String, url: String) {
        link[title] = url
    }

    fun note(fn: () -> String) {
        note = fn().trimIndent()
    }

    fun flow(purpose: String, complexity: Complexity = Complexity.SMALL, configure: FlowBuilder.() -> Unit = {}) =
        flows.add(FlowBuilder(purpose, complexity).apply(configure))

    fun build(id: String) =
        AcceptanceCriteria(
            id,
            description
                ?: throw IllegalArgumentException("{description} is required in AcceptanceCriteria!"),
            example,
            mockup,
            link,
            note,
            flows.mapIndexed { index, item -> item.build("$id-${index + 1}") }
        )
}
