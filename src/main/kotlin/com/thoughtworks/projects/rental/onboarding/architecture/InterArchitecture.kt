package com.thoughtworks.projects.rental.onboarding.architecture

import com.thoughtworks.blackhorse.schema.architecture.ArchitectureOf
import com.thoughtworks.blackhorse.schema.architecture.attributes.Responsibility

object RentalSystem : ArchitectureOf(
    Responsibility.Frontend to listOf(
        GeneralWeb
    ),
)
