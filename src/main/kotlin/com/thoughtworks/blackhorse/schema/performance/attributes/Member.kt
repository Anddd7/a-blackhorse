package com.thoughtworks.blackhorse.schema.performance.attributes

import java.time.LocalDate

/**
 * contact profile for team members
 */

open class Member(
    val name: String,
    val role: Role,
    val onboardedAt: LocalDate,
    val responsibility: String = "",
    val rolloffAt: LocalDate? = null,
    val stream: String? = null
)

enum class Role {
    BA, QA, TL, Dev, UX
}
