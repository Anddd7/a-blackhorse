package com.thoughtworks.projects.rental.onboarding.architecture.domain

import com.thoughtworks.blackhorse.schema.architecture.Container
import com.thoughtworks.blackhorse.schema.architecture.ProcessDefinitionBuilder
import com.thoughtworks.blackhorse.schema.architecture.attributes.Responsibility
import com.thoughtworks.blackhorse.schema.architecture.attributes.TechStack

object UserManagementService : Container(
    id = "53",
    layer = Responsibility.DomainService,
    techStack = listOf(
        TechStack("Spring Boot", "")
    ),
    responsibility = "用户账户管理系统",
) {
    override fun getDefinitions(): List<ProcessDefinitionBuilder> = emptyList()
}
