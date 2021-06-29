package com.thoughtworks.blackhorse.schema.performance

import com.thoughtworks.blackhorse.schema.story.AcceptanceCriteria
import com.thoughtworks.blackhorse.schema.story.Flow

class AcPerformanceBuilder(
    private val ac: AcceptanceCriteria,
) {
    private val flows = mutableListOf<FlowPerformanceBuilder>()

    fun flow(id: String? = null, fn: FlowPerformanceBuilder.() -> Unit) {
        val flow = id?.let(this::findFlowById) ?: findNextFlow()
        flows.add(FlowPerformanceBuilder(flow).apply(fn))
    }

    private fun findFlowById(id: String): Flow {
        return ac.flows.find { it.id == id }
            ?: throw IllegalArgumentException("No such flow with id: $id")
    }

    private fun findNextFlow(): Flow {
        if (flows.size == ac.flows.size)
            throw IllegalArgumentException("You have complete all flows, don't add invalid flow")
        return ac.flows[flows.size]
    }

    fun build() = flows.flatMap(FlowPerformanceBuilder::build)
}
