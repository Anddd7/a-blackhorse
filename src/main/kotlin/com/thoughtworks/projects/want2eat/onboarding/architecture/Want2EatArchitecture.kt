package com.thoughtworks.projects.want2eat.onboarding.architecture

import com.thoughtworks.blackhorse.schema.architecture.ArchitectureOf

object Want2EatArchitecture : ArchitectureOf(
    "0.0.3",
    """
        按规定格式书写工序设计
    """.trimIndent(),
    OrderService,
)

fun main() {
    Want2EatArchitecture.print()
}
