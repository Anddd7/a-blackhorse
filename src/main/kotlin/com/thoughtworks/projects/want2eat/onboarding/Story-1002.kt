package com.thoughtworks.projects.want2eat.onboarding

import com.thoughtworks.blackhorse.schema.story.StoryOf

object `Story-1002` : StoryOf(
    title = "Story-1002",
    cardId = "Story-1002",
    estimation = 3,
    configure = {
        inScope {
            """
            被投诉，扣减订单收入
            - 订单号，扣减数，扣减原因
            - 同时扣减余额
            """.trimIndent()
        }

        outOfScope {
            """
            ...
            """.trimIndent()
        }

        // ac
    },
    tracking = {
        decomposition("adliao", 80)
    }
)

fun main() {
    `Story-1002`.print()
}
