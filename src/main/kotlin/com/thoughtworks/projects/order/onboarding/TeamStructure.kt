package com.thoughtworks.projects.order.onboarding

import com.thoughtworks.blackhorse.schema.performance.attributes.Member
import com.thoughtworks.blackhorse.schema.performance.attributes.Role
import java.time.LocalDate

/**
 * define the team structure here, like name, role, stream, responsibility,
 * like a contact page for someone who needs help
 */

object ZhangSan : Member(
    "Zhang3",
    Role.Dev,
    LocalDate.parse("2021-01-01")
)

object LiSi : Member(
    "Li4",
    Role.Dev,
    LocalDate.parse("2021-01-01")
)
