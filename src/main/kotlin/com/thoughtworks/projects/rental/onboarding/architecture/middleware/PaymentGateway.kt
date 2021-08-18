package com.thoughtworks.projects.rental.onboarding.architecture.middleware

import com.thoughtworks.blackhorse.schema.architecture.Component
import com.thoughtworks.blackhorse.schema.architecture.Container
import com.thoughtworks.blackhorse.schema.architecture.ProcessDefBuilder
import com.thoughtworks.blackhorse.schema.architecture.attributes.ComponentLayer
import com.thoughtworks.blackhorse.schema.architecture.attributes.Responsibility
import com.thoughtworks.blackhorse.schema.architecture.attributes.TechStack

object PaymentGateway : Container(
    id = "61",
    layer = Responsibility.TechMiddleWare,
    techStack = listOf(
        TechStack("API Gateway", ""),
        TechStack("lambda", ""),
        TechStack("SQS", ""),
    ),
    responsibility = "支付网关: 负责接入三方支付平台,处理与三方支付平台的交互",
) {
    object EgressProxy : Component(
        ComponentLayer("Lambda", 0),
        listOf(
            TechStack("javascript"),
        ),
        "数据转换，调用第三方支付系统API"
    )

    object IngressProxy : Component(
        ComponentLayer("Lambda", 0),
        listOf(
            TechStack("javascript"),
        ),
        "数据转换，接收外部支付系统回调"
    )

    override fun getProcesses(): List<ProcessDefBuilder> = emptyList()
}
