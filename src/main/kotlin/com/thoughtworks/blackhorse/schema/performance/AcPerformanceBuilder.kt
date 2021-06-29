package com.thoughtworks.blackhorse.schema.performance

import com.thoughtworks.blackhorse.schema.story.AcceptanceCriteria
import com.thoughtworks.blackhorse.schema.story.Flow
import com.thoughtworks.blackhorse.utils.findByKeyOrNext

class AcPerformanceBuilder(
    private val ac: AcceptanceCriteria,
) {
    private val flows = mutableListOf<FlowPerformanceBuilder>()

    fun flow(id: String? = null, fn: FlowPerformanceBuilder.() -> Unit) {
        val flow = ac.flows.findByKeyOrNext(Flow::id, id, flows.size)
        flows.add(FlowPerformanceBuilder(flow).apply(fn))
    }

    fun build() = flows.flatMap(FlowPerformanceBuilder::build)
}
