package com.thoughtworks.blackhorse.schema.architecture.attributes

interface ContainerLayer {
    fun value(): String
    fun order(): Int
}

enum class BusinessSensitivity : ContainerLayer {
    TOUCH_POINT,
    DIFFERENTIATORS,
    CORE_BIZ_MODEL;

    override fun value(): String = name
    override fun order(): Int = ordinal
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
    override fun order(): Int = ordinal
}
