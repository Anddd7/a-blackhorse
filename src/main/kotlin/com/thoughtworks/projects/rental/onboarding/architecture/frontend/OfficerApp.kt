package com.thoughtworks.projects.rental.onboarding.architecture.frontend

import com.thoughtworks.blackhorse.schema.architecture.Container
import com.thoughtworks.blackhorse.schema.architecture.ProcessDefinitionBuilder
import com.thoughtworks.blackhorse.schema.architecture.attributes.Responsibility
import com.thoughtworks.blackhorse.schema.architecture.attributes.TechStack

object OfficerApp4Android : Container(
    id = "14",
    layer = Responsibility.Frontend,
    techStack = listOf(
        TechStack("Android", "build the app ui"),
        TechStack("Kotlin", "language support")
    ),
    responsibility = "思沃租房App 经纪人版 Android端",
) {
    override fun getDefinitions(): List<ProcessDefinitionBuilder> = emptyList()
}

object OfficerApp4IOS : Container(
    id = "15",
    layer = Responsibility.Frontend,
    techStack = listOf(
        TechStack("Swift", "build the app ui"),
    ),
    responsibility = "思沃租房App 经纪人版 IOS端",
) {
    override fun getDefinitions(): List<ProcessDefinitionBuilder> = emptyList()
}
