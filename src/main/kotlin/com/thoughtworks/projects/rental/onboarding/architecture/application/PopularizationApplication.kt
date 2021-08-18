package com.thoughtworks.projects.rental.onboarding.architecture.application

import com.thoughtworks.blackhorse.schema.architecture.Component
import com.thoughtworks.blackhorse.schema.architecture.Container
import com.thoughtworks.blackhorse.schema.architecture.ProcessDefBuilder
import com.thoughtworks.blackhorse.schema.architecture.attributes.Responsibility
import com.thoughtworks.blackhorse.schema.architecture.attributes.TechStack
import com.thoughtworks.projects.rental.onboarding.architecture.CommonComponentLayer

object PopularizationApplication : Container(
    id = "32",
    layer = Responsibility.ApplicationService,
    techStack = listOf(
        TechStack("Spring", "使用Spring生态搭建后端服务"),
        TechStack("Kotlin", "vs Java，语法简洁、并发支持更好"),
    ),
    responsibility = "推广服务应用服务: 向前端服务/应用提供推广服务和与充值服务相关的功能接口",
) {
    object Authentication : Component(
        CommonComponentLayer.Authentication,
        listOf(
            TechStack("Spring Security"),
        ),
        "与Authentication领域服务集成，验证用户身份"
    )

    object ApiController : Component(
        CommonComponentLayer.Controller,
        listOf(
            TechStack("Spring Mvc"),
        ),
        "接收API请求，数据校验，数据转换"
    )

    object Service : Component(
        CommonComponentLayer.Service,
        listOf(
            TechStack("POJO"),
        ),
        "业务逻辑处理"
    )

    object Vo : Component(
        CommonComponentLayer.Service,
        listOf(
            TechStack("Kotlin"),
        ),
        "数据模型对象"
    )

    object Client : Component(
        CommonComponentLayer.Client,
        listOf(
            TechStack("RestTemplate"),
        ),
        "HttpClient消费下游服务API"
    )

    object Dto : Component(
        CommonComponentLayer.Client,
        listOf(
            TechStack("POJO"),
        ),
        "下游服务数据模型对象"
    )

    override fun getProcesses(): List<ProcessDefBuilder> = emptyList()
}
