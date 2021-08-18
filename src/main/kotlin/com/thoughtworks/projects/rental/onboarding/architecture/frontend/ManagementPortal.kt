package com.thoughtworks.projects.rental.onboarding.architecture.frontend

import com.thoughtworks.blackhorse.schema.architecture.Container
import com.thoughtworks.blackhorse.schema.architecture.ProcessDefBuilder
import com.thoughtworks.blackhorse.schema.architecture.attributes.Responsibility
import com.thoughtworks.blackhorse.schema.architecture.attributes.TechStack

object ManagementPortal : Container(
    id = "16",
    layer = Responsibility.Frontend,
    techStack = listOf(
        TechStack("React", "build the ui components")
    ),
    responsibility = "后台管理系统Web端: 供思沃租房工作人员使用Web访问, 以完成后台管理功能",
) {
    override fun getProcesses(): List<ProcessDefBuilder> = emptyList()
}
