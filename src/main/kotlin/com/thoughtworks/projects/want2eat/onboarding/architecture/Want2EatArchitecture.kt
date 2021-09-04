package com.thoughtworks.projects.want2eat.onboarding.architecture

import com.thoughtworks.blackhorse.schema.architecture.ArchitectureOf

object Want2EatArchitecture : ArchitectureOf(
    "0.0.2",
    """
        工序拆分
    """.trimIndent(),
    OrderService,
)

fun main() {
    Want2EatArchitecture.print()
}
