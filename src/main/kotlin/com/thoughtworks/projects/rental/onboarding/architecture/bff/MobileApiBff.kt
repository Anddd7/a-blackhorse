package com.thoughtworks.projects.rental.onboarding.architecture.bff

import com.thoughtworks.blackhorse.schema.architecture.Container
import com.thoughtworks.blackhorse.schema.architecture.ProcessDefinitionBuilder
import com.thoughtworks.blackhorse.schema.architecture.attributes.Responsibility
import com.thoughtworks.blackhorse.schema.architecture.attributes.TechStack

object MobileApiBff : Container(
    id = "22",
    layer = Responsibility.BFF,
    techStack = listOf(
        TechStack("nodejs", "bff for mobile app")
    ),
    responsibility = "思沃租房 Mobile Bff",
) {
    override fun getDefinitions(): List<ProcessDefinitionBuilder> = emptyList()
}
