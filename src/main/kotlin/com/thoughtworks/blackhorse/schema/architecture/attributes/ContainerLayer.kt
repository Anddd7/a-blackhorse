package com.thoughtworks.blackhorse.schema.architecture.attributes

interface ContainerLayer {
    fun value(): String
}

enum class BusinessSensitivity : ContainerLayer {
    TOUCH_POINT,
    DIFFERENTIATORS,
    CORE_BIZ_MODEL;

    override fun value(): String = name
}

enum class Responsibility : ContainerLayer {
    Frontend,
    BFF,
    ApplicationService,
    CoreBizService,
    DomainService,
    TechMiddleWare,
    ThirdPartySystem;

    override fun value(): String = name
}
