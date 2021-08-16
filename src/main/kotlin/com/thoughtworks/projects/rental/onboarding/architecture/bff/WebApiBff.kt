package com.thoughtworks.projects.rental.onboarding.architecture.bff

import com.thoughtworks.blackhorse.schema.architecture.Container
import com.thoughtworks.blackhorse.schema.architecture.ProcessDefinitionBuilder
import com.thoughtworks.blackhorse.schema.architecture.attributes.Responsibility
import com.thoughtworks.blackhorse.schema.architecture.attributes.TechStack

object WebApiBff : Container(
    id = "21",
    layer = Responsibility.BFF,
    techStack = listOf(
        TechStack("go", "bff for web app")
    ),
    responsibility = "思沃租房 Web Bff",
) {
    override fun getDefinitions(): List<ProcessDefinitionBuilder> = emptyList()
}
