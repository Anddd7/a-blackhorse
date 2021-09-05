package com.thoughtworks.projects.want2eat.onboarding

import com.thoughtworks.blackhorse.schema.story.StoryOf
import com.thoughtworks.blackhorse.schema.story.attributes.Estimation

object `Story-1001` : StoryOf(
    title = "Story-1001",
    cardId = "Story-1001",
    estimation = 3,
    configure = {
        inScope {
            """
            完成订单，获得收入
            - 订单号，收入，时间，是否扣减，原因
            - 余额表变动
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
    `Story-1001`.print()
}
