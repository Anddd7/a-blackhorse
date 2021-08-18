package com.thoughtworks.projects.rental.onboarding.architecture.domain

import com.thoughtworks.blackhorse.schema.architecture.Container
import com.thoughtworks.blackhorse.schema.architecture.ProcessDefBuilder
import com.thoughtworks.blackhorse.schema.architecture.attributes.Responsibility
import com.thoughtworks.blackhorse.schema.architecture.attributes.TechStack

object UserManagementService : Container(
    id = "53",
    layer = Responsibility.DomainService,
    techStack = listOf(
        TechStack("Spring Boot", "")
    ),
    responsibility = "用戶账戶管理系统: 负责管理用戶账戶,包括常⻅的用戶账戶相关功能",
) {
    override fun getProcesses(): List<ProcessDefBuilder> = emptyList()
}
