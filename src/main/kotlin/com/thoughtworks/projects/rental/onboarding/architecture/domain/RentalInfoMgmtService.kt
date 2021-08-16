package com.thoughtworks.projects.rental.onboarding.architecture.domain

import com.thoughtworks.blackhorse.schema.architecture.Container
import com.thoughtworks.blackhorse.schema.architecture.ProcessDefinitionBuilder
import com.thoughtworks.blackhorse.schema.architecture.attributes.Responsibility
import com.thoughtworks.blackhorse.schema.architecture.attributes.TechStack

object RentalInfoMgmtService : Container(
    id = "52",
    layer = Responsibility.DomainService,
    techStack = listOf(
        TechStack("go", "bff for web app")
    ),
    responsibility = "房屋信息管理系统",
) {
    override fun getDefinitions(): List<ProcessDefinitionBuilder> = emptyList()
}
