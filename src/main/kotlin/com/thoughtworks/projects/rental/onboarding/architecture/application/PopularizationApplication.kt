package com.thoughtworks.projects.rental.onboarding.architecture.application

import com.thoughtworks.blackhorse.schema.architecture.Container
import com.thoughtworks.blackhorse.schema.architecture.ProcessDefinitionBuilder
import com.thoughtworks.blackhorse.schema.architecture.attributes.Responsibility
import com.thoughtworks.blackhorse.schema.architecture.attributes.TechStack

object PopularizationApplication : Container(
    id = "32",
    layer = Responsibility.ApplicationService,
    techStack = listOf(
        TechStack("Spring Boot", "")
    ),
    responsibility = "推广服务 应用服务",
) {
    override fun getDefinitions(): List<ProcessDefinitionBuilder> = emptyList()
}
