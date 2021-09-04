package com.thoughtworks.projects.want2eat.onboarding.team

import com.thoughtworks.blackhorse.schema.performance.attributes.Member
import com.thoughtworks.blackhorse.schema.performance.attributes.Role
import java.time.LocalDate

object Andong : Member(
    name = "Andong Liao",
    role = Role.Dev,
    onboardedAt = LocalDate.parse("2021-09-04"),
    responsibility = "全能搬砖工",
)
