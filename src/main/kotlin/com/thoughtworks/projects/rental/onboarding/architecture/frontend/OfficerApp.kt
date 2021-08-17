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
    responsibility = "思沃租房App经纪人版Android端: 供经纪人用戶使用Android端APP访问, 以完成经纪人相关的功能",
) {
    override fun getDefinitions(): List<ProcessDefinitionBuilder> = emptyList()
}

object OfficerApp4IOS : Container(
    id = "15",
    layer = Responsibility.Frontend,
    techStack = listOf(
        TechStack("Swift", "build the app ui"),
    ),
    responsibility = "思沃租房App经纪人版IOS端: 供经纪人用戶使用IOS端APP访问, 以完成经纪人相关的功能",
) {
    override fun getDefinitions(): List<ProcessDefinitionBuilder> = emptyList()
}
