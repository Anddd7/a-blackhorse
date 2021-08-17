package com.thoughtworks.projects.rental.onboarding.architecture.application

import com.thoughtworks.blackhorse.schema.architecture.Container
import com.thoughtworks.blackhorse.schema.architecture.ProcessDefinitionBuilder
import com.thoughtworks.blackhorse.schema.architecture.attributes.Responsibility
import com.thoughtworks.blackhorse.schema.architecture.attributes.TechStack

object ManagementApplication : Container(
    id = "31",
    layer = Responsibility.ApplicationService,
    techStack = listOf(
        TechStack("Spring Boot", "")
    ),
    responsibility = "后台管理应用服务: 向前端服务/应用提供后台管理的功能接口",
) {
    override fun getDefinitions(): List<ProcessDefinitionBuilder> = emptyList()
}
