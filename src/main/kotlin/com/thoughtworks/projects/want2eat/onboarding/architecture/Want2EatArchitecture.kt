package com.thoughtworks.projects.want2eat.onboarding.architecture

import com.thoughtworks.blackhorse.schema.architecture.ArchitectureOf

object Want2EatArchitecture : ArchitectureOf(
    "1.0.1",
    """
        准release
    """.trimIndent(),
    MerchantService,
)

fun main() {
    Want2EatArchitecture.print()
}
