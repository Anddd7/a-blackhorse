package com.thoughtworks.projects.rental.onboarding.architecture

import com.thoughtworks.blackhorse.schema.architecture.ArchitectureOf
import com.thoughtworks.projects.rental.onboarding.architecture.frontend.GeneralWebPortal
import com.thoughtworks.projects.rental.onboarding.architecture.frontend.IndividualApp4Android
import com.thoughtworks.projects.rental.onboarding.architecture.frontend.IndividualApp4IOS
import com.thoughtworks.projects.rental.onboarding.architecture.frontend.ManagementPortal
import com.thoughtworks.projects.rental.onboarding.architecture.frontend.OfficerApp4Android
import com.thoughtworks.projects.rental.onboarding.architecture.frontend.OfficerApp4IOS

object RentalSystem : ArchitectureOf(
    GeneralWebPortal,
    IndividualApp4Android,
    IndividualApp4IOS,
    OfficerApp4Android,
    OfficerApp4IOS,
    ManagementPortal,

)
