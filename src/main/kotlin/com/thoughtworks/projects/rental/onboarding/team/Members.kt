package com.thoughtworks.projects.rental.onboarding.team

import com.thoughtworks.blackhorse.schema.performance.attributes.Member
import com.thoughtworks.blackhorse.schema.performance.attributes.Role
import java.time.LocalDate

object Zhang3 : Member(
    name = "Zhang San",
    role = Role.Dev,
    onboardedAt = LocalDate.parse("2021-02-04"),
    responsibility = "搬砖",
)

object Li4 : Member(
    name = "Li Si",
    role = Role.TL,
    onboardedAt = LocalDate.parse("2021-01-01"),
    responsibility = "指挥搬砖",
)
