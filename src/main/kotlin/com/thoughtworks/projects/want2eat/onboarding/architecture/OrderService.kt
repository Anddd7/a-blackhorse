package com.thoughtworks.projects.want2eat.onboarding.architecture

import com.thoughtworks.blackhorse.schema.architecture.Component
import com.thoughtworks.blackhorse.schema.architecture.Container
import com.thoughtworks.blackhorse.schema.architecture.ProcessDefBuilder
import com.thoughtworks.blackhorse.schema.architecture.at
import com.thoughtworks.blackhorse.schema.architecture.attributes.ComponentLayer
import com.thoughtworks.blackhorse.schema.architecture.attributes.DomainLogic
import com.thoughtworks.blackhorse.schema.architecture.attributes.Responsibility
import com.thoughtworks.blackhorse.schema.architecture.attributes.TechStack

private object Layers {
    val Controller = ComponentLayer("Controller", 1)
    val Service = ComponentLayer("Service", 2)
    val Infrastructure = ComponentLayer("Infrastructure", 3)
    val External = ComponentLayer("External", 4)
}

object OrderService : Container(
    id = "1",
    layer = Responsibility.CoreBizService,
    techStack = listOf(
        TechStack("Spring Boot", ""),
        TechStack("PostgreSQL", "")
    ),
    domains = listOf(
        DomainLogic("价格计算引擎", "计算订单应支付的金额")
    ),
    responsibility = "餐品订购服务: 以订单为核心，为订餐用户提供下单、支付、退款、投诉等功能；商户可以接单、拒单，完成备餐后即可获得订单收益",
) {
    object Controller : Component(
        Layers.Controller,
        listOf(
            TechStack("Spring Mvc"),
        ),
        "定义API、校验请求数据，调用Service完成业务逻辑"
    )

    object Service : Component(
        Layers.Service,
        listOf(
            TechStack("Java"),
        ),
        "业务逻辑处理"
    )

    object ViewObject : Component(
        Layers.Service,
        listOf(
            TechStack("POJO"),
        ),
        "数据模型对象"
    )

    object Client : Component(
        Layers.Infrastructure,
        listOf(
            TechStack("RestTemplate"),
        ),
        "通过HTTP消费下游服务API"
    )

    object DTO : Component(
        Layers.Infrastructure,
        listOf(
            TechStack("POJO"),
        ),
        "下游服务数据模型对象"
    )

    object Repository : Component(
        Layers.Infrastructure,
        listOf(
            TechStack("Spring JPA"),
        ),
        "数据库访问"
    )

    object Entity : Component(
        Layers.Infrastructure,
        listOf(
            TechStack("POJO"),
        ),
        "数据库表模型"
    )

    object DB : Component(
        Layers.External,
        listOf(
            TechStack("AWS RDS"),
            TechStack("PostgreSQL"),
        ),
        "数据库"
    )

    object Gateway : Component(
        Layers.External,
        listOf(
            TechStack("AWS API Gateway"),
        ),
        "管理外部API"
    )

    object MQ : Component(
        Layers.External,
        listOf(
            TechStack("AWS SQS"),
            TechStack("AWS Lambda"),
        ),
        "消息队列"
    )

    object SpringBootTest : Component(
        Layers.Controller,
        emptyList(),
        "集成测试"
    )

    override fun getProcesses(): List<ProcessDefBuilder> = listOf(
        Controller mock Service at {
            """
                - Controller依赖Spring的运行环境，需要启动整个容器参与测试，以保证Controller配置了正确的API，接收请求调用Service并返回正确的Json数据；
                - ViewObject仅作为数据对象，验证其序列化的数据是否正确；
                
                测试时，会依赖：
                - 进程【内】组件【Service】，采用【Mock】来替代该组件，因为【需要保证对Service调用的入参和返回都正确】
            """.trimIndent()
        },
        Service mock Client at {
            """
                - Service只包含业务逻辑，可脱离框架进行单元测试；
                - ViewObject、DTO、Entity作为数据对象，验证数据转换时的结果是否正确；
                
                测试时，会依赖：
                - 进程【内】组件【Client】，采用【Mock】来替代该组件，因为【需要保证对Client调用的入参和返回都正确】
            """.trimIndent()
        },
        Service mock Repository at {
            """
                - Service只包含业务逻辑，可脱离框架进行单元测试；
                - ViewObject、DTO、Entity作为数据对象，验证数据转换时的结果是否正确；
                
                测试时，会依赖：
                - 进程【内】组件【Repository】，采用【Mock】来替代该组件，因为【需要保证对Repository调用的入参和返回都正确】
            """.trimIndent()
        },
        Client mock MQ at {
            """
                - Client需要进行HTTP调用，借助Spring框架可以快速完成测试；
                - DTO仅作为数据对象，验证其序列化的数据是否正确；

                测试时，会依赖：
                - 进程【外】组件【MQ】，采用【Mock】来替代该组件，因为【需要保证对外的HTTP调用的请求参数和返回值符合预期】
            """.trimIndent()
        },
        Client mock Gateway at {
            """
                - Client需要进行HTTP调用，借助Spring框架可以快速完成测试；
                - DTO仅作为数据对象，验证其序列化的数据是否正确；

                测试时，会依赖：
                - 进程【外】组件【Gateway】，采用【Mock】来替代该组件，因为【需要保证对外的HTTP调用的请求参数和返回值符合预期】
            """.trimIndent()
        },
        Repository fake DB at {
            """
                - Repository依赖Spring JPA，需要启动整个容器参与测试，以保证SQL语句的正确执行；
                - Entity仅作为数据对象，验证其序列化的数据是否正确；

                测试时，会依赖：
                - 进程【外】组件【PostgreSQL】，采用【Fake】来替代该组件，因为【数据库功能繁杂，mock的成本太高】
            """.trimIndent()
        },
        SpringBootTest call SpringBootTest at {
            """
                - 测试各个组件连接正确，并能够在Spring环境下正常运行；
                
                测试时，会依赖：
                - 进程【外】组件【MQ】，采用【Mock】来替代该组件，因为【需要保证对外的HTTP调用的请求参数和返回值符合预期】
                - 进程【外】组件【Gateway】，采用【Mock】来替代该组件，因为【需要保证对外的HTTP调用的请求参数和返回值符合预期】
                - 进程【外】组件【PostgreSQL】，采用【Fake】来替代该组件，因为【数据库功能繁杂，mock的成本太高】
            """.trimIndent()
        }
    )
}
