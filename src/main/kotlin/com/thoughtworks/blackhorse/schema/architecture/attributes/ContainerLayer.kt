package com.thoughtworks.blackhorse.schema.architecture.attributes

data class ContainerLayer(
    val value: String,
    val order: Int = 0,
)

object BusinessSensitivity {
    val TOUCH_POINT = ContainerLayer("TOUCH_POINT", 1)
    val DIFFERENTIATORS = ContainerLayer("DIFFERENTIATORS", 2)
    val CORE_BIZ_MODEL = ContainerLayer("CORE_BIZ_MODEL", 3)
}

object Responsibility {
    val Frontend = ContainerLayer("Frontend", 1)
    val BFF = ContainerLayer("BFF", 2)
    val ApplicationService = ContainerLayer("ApplicationService", 3)
    val CoreBizService = ContainerLayer("CoreBizService", 4)
    val DomainService = ContainerLayer("DomainService", 5)
    val TechMiddleWare = ContainerLayer("TechMiddleWare", 6)
    val ThirdPartySystem = ContainerLayer("ThirdPartySystem", 7)
}
