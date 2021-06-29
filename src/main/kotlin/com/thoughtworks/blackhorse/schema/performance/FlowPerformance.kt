package com.thoughtworks.blackhorse.schema.performance

import com.thoughtworks.blackhorse.config.CostAlgorithmOption
import com.thoughtworks.blackhorse.config.ProjectConfig
import com.thoughtworks.blackhorse.schema.story.AcceptanceCriteria
import com.thoughtworks.blackhorse.schema.story.Flow

data class FlowPerformance(
    val flowId: String,
    val order: Int,
    val expectCost: Int,
    val actualCost: Int,
    val blockCost: Int?,
    val blockType: BlockType?,
    val blockInstruction: String?,
    val blockConsequence: String?,
)

class FlowPerformanceBuilder(
    private val taskId: TaskId,
    private val flow: Flow
) {
    var title: String? = null
    var actualCost: Int = 0
    var blockCost: Int? = null
    var blockType: BlockType? = null
    var blockInstruction: String? = null
    var blockConsequence: String? = null

    fun build() =
        FlowPerformance(
            flow.id,
            taskId.order(),
            getExpectCost(),
            actualCost,
            blockCost,
            blockType,
            blockInstruction,
            blockConsequence
        )

    private fun getExpectCost() = when (ProjectConfig.costAlgorithm()) {
        CostAlgorithmOption.FLOW -> flow.complexity.minutes
        CostAlgorithmOption.PROCESS -> flow.processes.sumOf { it.definition?.cost ?: 0 }
    }
}

infix fun FlowPerformanceBuilder.cost(cost: Int) = apply { actualCost = cost }
infix fun FlowPerformanceBuilder.cost(cost: Double) = apply { actualCost = cost.toInt() }
infix fun FlowPerformanceBuilder.blocked(cost: Int) = apply { blockCost = cost }
infix fun FlowPerformanceBuilder.by(type: BlockType) = apply { blockType = type }
infix fun FlowPerformanceBuilder.cause(string: String) = apply { blockInstruction = string }
infix fun FlowPerformanceBuilder.resolved(string: String) = apply { blockConsequence = string }
infix fun FlowPerformanceBuilder.resolved(fn: () -> String) = apply { blockConsequence = fn() }

class AcPerformanceBuilder(
    private val acId: Int,
    private val ac: AcceptanceCriteria,
) {
    val tasks = mutableListOf<FlowPerformanceBuilder>()

    fun flow(): FlowPerformanceBuilder {
        val flowIndex = tasks.size
        val flow = ac.flows[flowIndex]
        val taskPerformance = FlowPerformanceBuilder(TaskId(acId, flowIndex + 1), flow)
        return taskPerformance.also(tasks::add)
    }
}
