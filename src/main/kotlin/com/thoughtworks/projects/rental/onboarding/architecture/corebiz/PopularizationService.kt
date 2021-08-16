package com.thoughtworks.projects.rental.onboarding.architecture.corebiz

import com.thoughtworks.blackhorse.schema.architecture.Container
import com.thoughtworks.blackhorse.schema.architecture.ProcessDefinitionBuilder
import com.thoughtworks.blackhorse.schema.architecture.attributes.DomainLogic
import com.thoughtworks.blackhorse.schema.architecture.attributes.Responsibility
import com.thoughtworks.blackhorse.schema.architecture.attributes.TechStack

object PopularizationService : Container(
    id = "41",
    layer = Responsibility.CoreBizService,
    techStack = listOf(
        TechStack("Spring Boot", "")
    ),
    domains = listOf(
        DomainLogic("推广报价引擎", "计算推广的价格")
    ),
    responsibility = "信息推广服务",
) {
    override fun getDefinitions(): List<ProcessDefinitionBuilder> = emptyList()
}
