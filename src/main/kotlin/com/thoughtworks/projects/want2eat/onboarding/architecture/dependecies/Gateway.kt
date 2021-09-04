package com.thoughtworks.projects.want2eat.onboarding.architecture.dependecies

import com.thoughtworks.blackhorse.schema.architecture.Component
import com.thoughtworks.blackhorse.schema.architecture.Container
import com.thoughtworks.blackhorse.schema.architecture.ProcessDefBuilder
import com.thoughtworks.blackhorse.schema.architecture.attributes.ComponentLayer
import com.thoughtworks.blackhorse.schema.architecture.attributes.Responsibility
import com.thoughtworks.blackhorse.schema.architecture.attributes.TechStack

object Gateway : Container(
    id = "12",
    layer = Responsibility.TechMiddleWare,
    techStack = listOf(
        TechStack("API Gateway", "")
    ),
    responsibility = "三方服务网关: 对接的三方平台或系统",
) {
    object ApiGateway : Component(
        ComponentLayer("API Gateway", 0),
        listOf(
            TechStack("API Gateway"),
        ),
        "API网关：管理需要对接的外部API"
    )

    override fun getProcesses(): List<ProcessDefBuilder> = emptyList()
}
