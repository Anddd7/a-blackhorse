package com.thoughtworks.projects.rental.onboarding.architecture.middleware

import com.thoughtworks.blackhorse.schema.architecture.Component
import com.thoughtworks.blackhorse.schema.architecture.Container
import com.thoughtworks.blackhorse.schema.architecture.ProcessDefBuilder
import com.thoughtworks.blackhorse.schema.architecture.attributes.ComponentLayer
import com.thoughtworks.blackhorse.schema.architecture.attributes.Responsibility
import com.thoughtworks.blackhorse.schema.architecture.attributes.TechStack

object MessageQueue : Container(
    id = "63",
    layer = Responsibility.TechMiddleWare,
    responsibility = "消息队列：缓存消息",
) {
    object SQS : Component(
        ComponentLayer("SQS", 0),
        listOf(
            TechStack("AWS SQS"),
        ),
        "数据转换，调用第三方支付系统API"
    )

    object Lambda : Component(
        ComponentLayer("Lambda", 1),
        listOf(
            TechStack("javascript"),
        ),
        "数据转换，接收外部支付系统回调"
    )

    override fun getProcesses(): List<ProcessDefBuilder> = emptyList()
}
