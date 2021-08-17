package com.thoughtworks.projects.rental.onboarding.architecture.application

import com.thoughtworks.blackhorse.schema.architecture.Component
import com.thoughtworks.blackhorse.schema.architecture.Container
import com.thoughtworks.blackhorse.schema.architecture.ProcessDefinitionBuilder
import com.thoughtworks.blackhorse.schema.architecture.attributes.ComponentLayer
import com.thoughtworks.blackhorse.schema.architecture.attributes.Responsibility
import com.thoughtworks.blackhorse.schema.architecture.attributes.TechStack

private object CommonComponentLayer {
    val Authentication = ComponentLayer("Authentication", 0)
    val Controller = ComponentLayer("Controller", 1)
    val Service = ComponentLayer("Service", 2)
    val Client = ComponentLayer("Client", 3)
}

object PopularizationApplication : Container(
    id = "32",
    layer = Responsibility.ApplicationService,
    techStack = listOf(
        TechStack("Spring", "使用Spring生态搭建后端服务"),
        TechStack("Kotlin", "vs Java，语法简洁、并发支持更好"),
    ),
    responsibility = "推广服务 应用服务",
) {
    object Authentication : Component(
        CommonComponentLayer.Authentication,
        listOf(
            TechStack("Spring Security", "验证用户身份"),
        ),
        "验证用户身份：仅限已注册经纪人访问"
    )

    object ApiController : Component(
        CommonComponentLayer.Controller,
        listOf(
            TechStack("Spring Mvc"),
        ),
        "基于服务功能构建API"
    )

    object Service : Component(
        CommonComponentLayer.Service,
        listOf(
            TechStack("Kotlin"),
        ),
        "基于服务功能构建API"
    )

    object Vo : Component(CommonComponentLayer.Service)

    object Client : Component(CommonComponentLayer.Client)
    object Dto : Component(CommonComponentLayer.Client)

    override fun getDefinitions(): List<ProcessDefinitionBuilder> = emptyList()
}
