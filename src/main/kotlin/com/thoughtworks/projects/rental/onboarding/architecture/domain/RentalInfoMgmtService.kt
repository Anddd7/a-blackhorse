package com.thoughtworks.projects.rental.onboarding.architecture.domain

import com.thoughtworks.blackhorse.schema.architecture.Container
import com.thoughtworks.blackhorse.schema.architecture.ProcessDefBuilder
import com.thoughtworks.blackhorse.schema.architecture.attributes.Responsibility
import com.thoughtworks.blackhorse.schema.architecture.attributes.TechStack

object RentalInfoMgmtService : Container(
    id = "52",
    layer = Responsibility.DomainService,
    techStack = listOf(
        TechStack("Strapi", "cms")
    ),
    responsibility = "房屋信息管理系统: 存储、查询和管理房屋租赁信息的 CMS 系统",
) {
    override fun getProcesses(): List<ProcessDefBuilder> = emptyList()
}
