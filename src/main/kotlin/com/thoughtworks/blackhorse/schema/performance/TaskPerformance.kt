package com.thoughtworks.blackhorse.schema.performance

import com.thoughtworks.blackhorse.config.CostAlgorithmOption
import com.thoughtworks.blackhorse.config.ProjectConfig
import com.thoughtworks.blackhorse.schema.story.AcceptanceCriteria
import com.thoughtworks.blackhorse.schema.story.Flow

data class TaskPerformance(
    val order: Int,
    val title: String,
    val expectCost: Int,
    val actualCost: Int,
    val blockCost: Int?,
    val blockType: BlockType?,
    val blockInstruction: String?,
    val blockConsequence: String?,
)

class TaskPerformanceBuilder(
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
        TaskPerformance(
            taskId.order(),
            flow.purpose,
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

infix fun TaskPerformanceBuilder.cost(cost: Int) = apply { actualCost = cost }
infix fun TaskPerformanceBuilder.cost(cost: Double) = apply { actualCost = cost.toInt() }
infix fun TaskPerformanceBuilder.blocked(cost: Int) = apply { blockCost = cost }
infix fun TaskPerformanceBuilder.by(type: BlockType) = apply { blockType = type }
infix fun TaskPerformanceBuilder.cause(string: String) = apply { blockInstruction = string }
infix fun TaskPerformanceBuilder.resolved(string: String) = apply { blockConsequence = string }
infix fun TaskPerformanceBuilder.resolved(fn: () -> String) = apply { blockConsequence = fn() }

class AcPerformanceBuilder(
    private val acId: Int,
    private val ac: AcceptanceCriteria,
) {
    val tasks = mutableListOf<TaskPerformanceBuilder>()

    fun flow(): TaskPerformanceBuilder {
        val flowIndex = tasks.size
        val flow = ac.flows[flowIndex]
        val taskPerformance = TaskPerformanceBuilder(TaskId(acId, flowIndex + 1), flow)
        return taskPerformance.also(tasks::add)
    }
}
