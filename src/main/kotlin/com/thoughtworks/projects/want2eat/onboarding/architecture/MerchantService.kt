package com.thoughtworks.projects.want2eat.onboarding.architecture

import com.thoughtworks.blackhorse.schema.architecture.Component
import com.thoughtworks.blackhorse.schema.architecture.Container
import com.thoughtworks.blackhorse.schema.architecture.ProcessDefBuilder
import com.thoughtworks.blackhorse.schema.architecture.at
import com.thoughtworks.blackhorse.schema.architecture.attributes.ComponentLayer
import com.thoughtworks.blackhorse.schema.architecture.attributes.Responsibility
import com.thoughtworks.blackhorse.schema.architecture.attributes.TechStack
import com.thoughtworks.blackhorse.schema.architecture.cost

private object Layers {
    val Controller = ComponentLayer("Controller", 1)
    val Service = ComponentLayer("Service", 2)
    val Infrastructure = ComponentLayer("Infrastructure", 3)
    val External = ComponentLayer("External", 4)
}

object MerchantService : Container(
    id = "1",
    layer = Responsibility.CoreBizService,
    techStack = listOf(
        TechStack("Spring Boot", ""),
        TechStack("PostgreSQL", "")
    ),
    domains = emptyList(),
    responsibility = "餐品订购服务: 为商家提供接入平台的服务，包括开通账号、缴纳押金、提现入账余额、收据和发票开具的功能；平台可对违反合作协议的商家进行押金扣减、入账扣减",
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
        Controller mock Service cost 15 at {
            """
                实现Controller获取Http请求参数，调用Service并获取ViewObject，再返回序列化的Json数据
            """.trimIndent()
        },
        Service mock Client cost 20 at {
            """
                实现Service调用Client获取DTO，组装成ViewObject并返回
            """.trimIndent()
        },
        Service mock Repository cost 20 at {
            """
                实现Service调用Repository获取Entity，组装成ViewObject并返回
            """.trimIndent()
        },
        Client mock MQ cost 25 at {
            """
                实现Client调用MQ，通过DTO映射请求和返回的Json数据，验证发送和接收的数据正确
            """.trimIndent()
        },
        Client mock Gateway cost 25 at {
            """
               实现Client调用Gateway，通过DTO映射请求和返回的Json数据
            """.trimIndent()
        },
        Repository fake DB cost 30 at {
            """
               实现Repository调用DB，通过Entity映射数据库表，验证JPA的配置正确、数据库表创建正确、SQL语句书写正确
            """.trimIndent()
        },
        SpringBootTest call SpringBootTest cost 60 at {
            """
               实现多个组件在Spring环境下的集成测试，验证框架的功能：拦截器、AOP、日志、事务处理
            """.trimIndent()
        }
    )
}