package com.thoughtworks.projects.rental.onboarding.architecture.domain

import com.thoughtworks.blackhorse.schema.architecture.Component
import com.thoughtworks.blackhorse.schema.architecture.Container
import com.thoughtworks.blackhorse.schema.architecture.ProcessDefinitionBuilder
import com.thoughtworks.blackhorse.schema.architecture.attributes.Responsibility
import com.thoughtworks.blackhorse.schema.architecture.attributes.TechStack
import com.thoughtworks.projects.rental.onboarding.architecture.CommonComponentLayer

object AuthenticationService : Container(
    id = "51",
    layer = Responsibility.DomainService,
    techStack = listOf(
        TechStack("Spring Boot", "")
    ),
    responsibility = "鉴权认证服务: 负责提供用戶的身份和鉴权等功能",
) {

    object ApiController : Component(
        CommonComponentLayer.Controller,
        listOf(
            TechStack("Spring Mvc"),
        ),
        "接收API请求，数据校验，数据转换"
    )

    override fun getDefinitions(): List<ProcessDefinitionBuilder> = emptyList()
}
