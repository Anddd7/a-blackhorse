package com.thoughtworks.projects.rental.onboarding.architecture.corebiz

import com.thoughtworks.blackhorse.schema.architecture.Container
import com.thoughtworks.blackhorse.schema.architecture.ProcessDefinitionBuilder
import com.thoughtworks.blackhorse.schema.architecture.attributes.Responsibility
import com.thoughtworks.blackhorse.schema.architecture.attributes.TechStack

object PrepaidService : Container(
    id = "42",
    layer = Responsibility.CoreBizService,
    techStack = listOf(
        TechStack("Spring Boot", "")
    ),
    responsibility = "预充值服务",
) {
    override fun getDefinitions(): List<ProcessDefinitionBuilder> = emptyList()
}
