package com.thoughtworks.projects.rental.onboarding.architecture.frontend

import com.thoughtworks.blackhorse.schema.architecture.Container
import com.thoughtworks.blackhorse.schema.architecture.ProcessDefinitionBuilder
import com.thoughtworks.blackhorse.schema.architecture.attributes.Responsibility
import com.thoughtworks.blackhorse.schema.architecture.attributes.TechStack

object IndividualApp4Android : Container(
    id = "12",
    layer = Responsibility.Frontend,
    techStack = listOf(
        TechStack("Android", "build the app ui"),
        TechStack("Kotlin", "language support")
    ),
    responsibility = "思沃租房App个人版Android端: 供浏览用戶、个人用戶使用Android端APP访问, 以完成个人用戶相关的功能",
) {
    override fun getDefinitions(): List<ProcessDefinitionBuilder> = emptyList()
}

object IndividualApp4IOS : Container(
    id = "13",
    layer = Responsibility.Frontend,
    techStack = listOf(
        TechStack("Swift", "build the app ui"),
    ),
    responsibility = "思沃租房App个人版IOS端: 供浏览用戶、个人用戶使用IOS端APP访问, 以完成个人用戶相关的功能",
) {
    override fun getDefinitions(): List<ProcessDefinitionBuilder> = emptyList()
}

