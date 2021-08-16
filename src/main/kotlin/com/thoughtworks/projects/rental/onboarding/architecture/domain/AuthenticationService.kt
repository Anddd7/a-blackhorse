package com.thoughtworks.projects.rental.onboarding.architecture.domain

import com.thoughtworks.blackhorse.schema.architecture.Container
import com.thoughtworks.blackhorse.schema.architecture.ProcessDefinitionBuilder
import com.thoughtworks.blackhorse.schema.architecture.attributes.Responsibility
import com.thoughtworks.blackhorse.schema.architecture.attributes.TechStack

object AuthenticationService : Container(
    id = "51",
    layer = Responsibility.DomainService,
    techStack = listOf(
        TechStack("Spring Boot", "")
    ),
    responsibility = "鉴权认证服务",
) {
    override fun getDefinitions(): List<ProcessDefinitionBuilder> = emptyList()
}
