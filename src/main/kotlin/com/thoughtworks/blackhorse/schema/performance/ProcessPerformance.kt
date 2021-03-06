package com.thoughtworks.blackhorse.schema.performance

import com.thoughtworks.blackhorse.schema.performance.attributes.BlockType
import com.thoughtworks.blackhorse.schema.story.Task

data class ProcessPerformance(
    val id: String,
    val flowId: String,
    val processName: String,
    val expectCost: Int,
    val actualCost: Int,
    val blockCost: Int?,
    val blockType: BlockType?,
    val blockInstruction: String?,
    val blockConsequence: String?,
)

class ProcessPerformanceBuilder(
    private val process: Task,
) {
    fun build(flowId: String) =
        ProcessPerformance(
            process.id,
            flowId,
            process.process?.name ?: "",
            process.process?.complexity?.cost ?: 0,
            actualCost,
            blockCost,
            blockType,
            blockInstruction,
            blockConsequence
        )

    var actualCost: Int = 0
    var blockCost: Int? = null
    var blockType: BlockType? = null
    var blockInstruction: String? = null
    var blockConsequence: String? = null
}

infix fun ProcessPerformanceBuilder.cost(cost: Int) = apply { actualCost = cost }
infix fun ProcessPerformanceBuilder.cost(cost: Double) = apply { actualCost = cost.toInt() }
infix fun ProcessPerformanceBuilder.blocked(cost: Int) = apply { blockCost = cost }
infix fun ProcessPerformanceBuilder.by(type: BlockType) = apply { blockType = type }
infix fun ProcessPerformanceBuilder.cause(string: String) = apply { blockInstruction = string }
infix fun ProcessPerformanceBuilder.resolved(string: String) = apply { blockConsequence = string }
infix fun ProcessPerformanceBuilder.resolved(fn: () -> String) = apply { blockConsequence = fn() }
