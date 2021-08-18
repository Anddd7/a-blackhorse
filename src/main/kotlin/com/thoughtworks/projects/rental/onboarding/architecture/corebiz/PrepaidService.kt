package com.thoughtworks.projects.rental.onboarding.architecture.corebiz

import com.thoughtworks.blackhorse.schema.architecture.Component
import com.thoughtworks.blackhorse.schema.architecture.Container
import com.thoughtworks.blackhorse.schema.architecture.ProcessDefBuilder
import com.thoughtworks.blackhorse.schema.architecture.attributes.Responsibility
import com.thoughtworks.blackhorse.schema.architecture.attributes.TechStack
import com.thoughtworks.projects.rental.onboarding.architecture.CommonComponentLayer

object PrepaidService : Container(
    id = "42",
    layer = Responsibility.CoreBizService,
    techStack = listOf(
        TechStack("Spring Boot", "")
    ),
    responsibility = "预充值服务: 提供预充值服务协议上下文中的业务能力接口",
) {

    object ApiController : Component(
        CommonComponentLayer.Controller,
        listOf(
            TechStack("Spring Mvc"),
        ),
        "接收API请求，数据校验，数据转换"
    )

    override fun getProcesses(): List<ProcessDefBuilder> = emptyList()
}
