package com.thoughtworks.projects.rental.onboarding.architecture.frontend

import com.thoughtworks.blackhorse.schema.architecture.Container
import com.thoughtworks.blackhorse.schema.architecture.ProcessDefBuilder
import com.thoughtworks.blackhorse.schema.architecture.attributes.Responsibility
import com.thoughtworks.blackhorse.schema.architecture.attributes.TechStack

object GeneralWebPortal : Container(
    id = "11",
    layer = Responsibility.Frontend,
    techStack = listOf(
        TechStack("React", "build the ui components")
    ),
    responsibility = "思沃租房通用版Web端: 供浏览用戶、个人用戶和经纪人用戶使用Web访问",
) {
    override fun getProcesses(): List<ProcessDefBuilder> = emptyList()
}
