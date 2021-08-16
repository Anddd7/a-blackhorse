package com.thoughtworks.projects.rental.onboarding.architecture.middleware

import com.thoughtworks.blackhorse.schema.architecture.Container
import com.thoughtworks.blackhorse.schema.architecture.ProcessDefinitionBuilder
import com.thoughtworks.blackhorse.schema.architecture.attributes.Responsibility
import com.thoughtworks.blackhorse.schema.architecture.attributes.TechStack

object PaymentGateway : Container(
    id = "61",
    layer = Responsibility.TechMiddleWare,
    techStack = listOf(
        TechStack("lambda", "")
    ),
    responsibility = "支付网关",
) {
    override fun getDefinitions(): List<ProcessDefinitionBuilder> = emptyList()
}

