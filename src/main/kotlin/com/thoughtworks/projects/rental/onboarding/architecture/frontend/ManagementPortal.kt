package com.thoughtworks.projects.rental.onboarding.architecture.frontend

import com.thoughtworks.blackhorse.schema.architecture.Container
import com.thoughtworks.blackhorse.schema.architecture.ProcessDefinitionBuilder
import com.thoughtworks.blackhorse.schema.architecture.attributes.Responsibility
import com.thoughtworks.blackhorse.schema.architecture.attributes.TechStack

object ManagementPortal : Container(
    id = "16",
    layer = Responsibility.Frontend,
    techStack = listOf(
        TechStack("React", "build the ui components")
    ),
    responsibility = "后台管理系统 Web端",
) {
    override fun getDefinitions(): List<ProcessDefinitionBuilder> = emptyList()
}
