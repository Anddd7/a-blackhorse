package com.thoughtworks.projects.rental.onboarding.architecture.bff

import com.thoughtworks.blackhorse.schema.architecture.Container
import com.thoughtworks.blackhorse.schema.architecture.ProcessDefBuilder
import com.thoughtworks.blackhorse.schema.architecture.attributes.Responsibility
import com.thoughtworks.blackhorse.schema.architecture.attributes.TechStack

object WebApiBff : Container(
    id = "21",
    layer = Responsibility.BFF,
    techStack = listOf(
        TechStack("go", "bff for web app")
    ),
    responsibility = "思沃租房Web Bff: 基于后端应用服务, 服务于思沃租房通用版Web端",
) {
    override fun getProcesses(): List<ProcessDefBuilder> = emptyList()
}
