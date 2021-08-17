package com.thoughtworks.projects.rental.onboarding.architecture.bff

import com.thoughtworks.blackhorse.schema.architecture.Container
import com.thoughtworks.blackhorse.schema.architecture.ProcessDefinitionBuilder
import com.thoughtworks.blackhorse.schema.architecture.attributes.Responsibility
import com.thoughtworks.blackhorse.schema.architecture.attributes.TechStack

object MobileApiBff : Container(
    id = "22",
    layer = Responsibility.BFF,
    techStack = listOf(
        TechStack("nodejs", "bff for mobile app")
    ),
    responsibility = "思沃租房Mobile Bff: 基于后端应用服务, 服务于思沃租房通APP, 包括个人版和经纪人版, Android和IOS端",
) {
    override fun getDefinitions(): List<ProcessDefinitionBuilder> = emptyList()
}
