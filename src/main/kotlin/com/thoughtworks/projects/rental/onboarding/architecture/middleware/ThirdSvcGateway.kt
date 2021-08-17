package com.thoughtworks.projects.rental.onboarding.architecture.middleware

import com.thoughtworks.blackhorse.schema.architecture.Container
import com.thoughtworks.blackhorse.schema.architecture.ProcessDefinitionBuilder
import com.thoughtworks.blackhorse.schema.architecture.attributes.Responsibility
import com.thoughtworks.blackhorse.schema.architecture.attributes.TechStack

object ThirdSvcGateway : Container(
    id = "62",
    layer = Responsibility.TechMiddleWare,
    techStack = listOf(
        TechStack("lambda", "")
    ),
    responsibility = "三方服务网关: 对接除支付外的三方平台",
) {
    override fun getDefinitions(): List<ProcessDefinitionBuilder> = emptyList()
}
