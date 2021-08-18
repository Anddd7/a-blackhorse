package com.thoughtworks.projects.rental.onboarding.architecture.application

import com.thoughtworks.blackhorse.schema.architecture.Container
import com.thoughtworks.blackhorse.schema.architecture.ProcessDefBuilder
import com.thoughtworks.blackhorse.schema.architecture.attributes.Responsibility
import com.thoughtworks.blackhorse.schema.architecture.attributes.TechStack

object RentalApplication : Container(
    id = "33",
    layer = Responsibility.ApplicationService,
    techStack = listOf(
        TechStack("Spring Boot", "")
    ),
    responsibility = "租赁信息应用服务: 向前端服务/应用提供租赁信息的展示、搜索、发布、更新、下架等功能接口",
) {
    override fun getProcesses(): List<ProcessDefBuilder> = emptyList()
}
