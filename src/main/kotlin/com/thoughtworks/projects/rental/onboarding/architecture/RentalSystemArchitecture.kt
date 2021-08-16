package com.thoughtworks.projects.rental.onboarding.architecture

import com.thoughtworks.blackhorse.schema.architecture.ArchitectureOf
import com.thoughtworks.projects.rental.onboarding.architecture.application.ManagementApplication
import com.thoughtworks.projects.rental.onboarding.architecture.application.PopularizationApplication
import com.thoughtworks.projects.rental.onboarding.architecture.application.RentalApplication
import com.thoughtworks.projects.rental.onboarding.architecture.bff.MobileApiBff
import com.thoughtworks.projects.rental.onboarding.architecture.bff.WebApiBff
import com.thoughtworks.projects.rental.onboarding.architecture.corebiz.PopularizationService
import com.thoughtworks.projects.rental.onboarding.architecture.corebiz.PrepaidService
import com.thoughtworks.projects.rental.onboarding.architecture.domain.AuthenticationService
import com.thoughtworks.projects.rental.onboarding.architecture.domain.RentalInfoMgmtService
import com.thoughtworks.projects.rental.onboarding.architecture.domain.UserManagementService
import com.thoughtworks.projects.rental.onboarding.architecture.frontend.GeneralWebPortal
import com.thoughtworks.projects.rental.onboarding.architecture.frontend.IndividualApp4Android
import com.thoughtworks.projects.rental.onboarding.architecture.frontend.IndividualApp4IOS
import com.thoughtworks.projects.rental.onboarding.architecture.frontend.ManagementPortal
import com.thoughtworks.projects.rental.onboarding.architecture.frontend.OfficerApp4Android
import com.thoughtworks.projects.rental.onboarding.architecture.frontend.OfficerApp4IOS

object RentalSystemArchitecture : ArchitectureOf(
    "1.0",
    "initial design",
    // frontend
    GeneralWebPortal,
    IndividualApp4Android,
    IndividualApp4IOS,
    OfficerApp4Android,
    OfficerApp4IOS,
    ManagementPortal,
    // bff
    MobileApiBff,
    WebApiBff,
    // application
    ManagementApplication,
    PopularizationApplication,
    RentalApplication,
    // core biz
    PopularizationService,
    PrepaidService,
    // domain
    AuthenticationService,
    RentalInfoMgmtService,
    UserManagementService
)

fun main() {
    RentalSystemArchitecture.print()
}
