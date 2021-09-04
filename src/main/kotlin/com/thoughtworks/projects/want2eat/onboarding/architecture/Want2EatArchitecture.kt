package com.thoughtworks.projects.want2eat.onboarding.architecture

import com.thoughtworks.blackhorse.schema.architecture.ArchitectureOf

object Want2EatArchitecture : ArchitectureOf(
    "0.0.1",
    """
        绘制需要的架构信息
    """.trimIndent(),
    OrderService,
)

fun main() {
    Want2EatArchitecture.print()
}
