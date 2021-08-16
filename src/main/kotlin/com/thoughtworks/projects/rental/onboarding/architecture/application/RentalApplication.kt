package com.thoughtworks.projects.rental.onboarding.architecture.application

import com.thoughtworks.blackhorse.schema.architecture.Container
import com.thoughtworks.blackhorse.schema.architecture.ProcessDefinitionBuilder
import com.thoughtworks.blackhorse.schema.architecture.attributes.Responsibility
import com.thoughtworks.blackhorse.schema.architecture.attributes.TechStack

object RentalApplication : Container(
    id = "33",
    layer = Responsibility.ApplicationService,
    techStack = listOf(
        TechStack("Spring Boot", "")
    ),
    responsibility = "租赁信息 应用服务",
) {
    override fun getDefinitions(): List<ProcessDefinitionBuilder> = emptyList()
}

