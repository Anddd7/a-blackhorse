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
                实现Controller获取Http请求参数，调用Service并获取ViewObject，再返回序列化的Json数据
            """.trimIndent()
        },
        Service mock Client at {
            """
                实现Service调用Client获取DTO，组装成ViewObject并返回
            """.trimIndent()
        },
        Service mock Repository at {
            """
                实现Service调用Repository获取Entity，组装成ViewObject并返回
            """.trimIndent()
        },
        Client mock MQ at {
            """
                实现Client调用MQ，通过DTO映射请求和返回的Json数据
            """.trimIndent()
        },
        Client mock Gateway at {
            """
               实现Client调用Gateway，通过DTO映射请求和返回的Json数据
            """.trimIndent()
        },
        Repository fake DB at {
            """
               实现Repository调用DB，通过JPA正确执行数据库访问并返回对应的Entity数据
            """.trimIndent()
        },
        SpringBootTest call SpringBootTest at {
            """
               实现所有组件进行集成测试
            """.trimIndent()
        }
    )
}
