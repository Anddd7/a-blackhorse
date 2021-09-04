package com.thoughtworks.projects.want2eat.onboarding.architecture.dependecies

import com.thoughtworks.blackhorse.schema.architecture.Component
import com.thoughtworks.blackhorse.schema.architecture.Container
import com.thoughtworks.blackhorse.schema.architecture.ProcessDefBuilder
import com.thoughtworks.blackhorse.schema.architecture.attributes.ComponentLayer
import com.thoughtworks.blackhorse.schema.architecture.attributes.Responsibility
import com.thoughtworks.blackhorse.schema.architecture.attributes.TechStack

object MQ : Container(
    id = "11",
    layer = Responsibility.TechMiddleWare,
    responsibility = "消息队列：提供缓存、重试、投递消息的能力",
) {
    object SQS : Component(
        ComponentLayer("SQS", 0),
        listOf(
            TechStack("AWS SQS"),
        ),
        "队列：缓存消息"
    )

    object Lambda : Component(
        ComponentLayer("Lambda", 1),
        listOf(
            TechStack("javascript"),
        ),
        "执行器：执行消息"
    )

    override fun getProcesses(): List<ProcessDefBuilder> = emptyList()
}
