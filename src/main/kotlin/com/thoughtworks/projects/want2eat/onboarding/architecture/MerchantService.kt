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
    object Controller : Component(Layers.Controller)
    object Service : Component(Layers.Service)
    object RepositoryClient : Component(Layers.Infrastructure)
    object MQGateway : Component(Layers.External)
    object DB : Component(Layers.External)
    object SpringBootTest : Component(Layers.External)

    override fun getProcesses(): List<ProcessDefBuilder> = listOf(
        Controller mock Service cost 15 at {
            """
              实现Controller获取Http请求参数，调用Service并获取ViewObject，再返回序列化的Json数据
            """.trimIndent()
        },
        Service mock RepositoryClient cost 30 at {
            """
                实现Service调用Repository获取Entity / Client获取DTO，组装成ViewObject并返回
            """.trimIndent()
        },
        RepositoryClient mock MQGateway cost 25 at {
            """
                实现Client使用Http调用MQ或Gateway，通过DTO映射请求和返回的Json数据
            """.trimIndent()
        },
        RepositoryClient fake DB cost 30 at {
            """
                实现Repository使用JPA调用DB，通过Entity映射数据库表数据
            """.trimIndent()
        },
        SpringBootTest call SpringBootTest cost 60 at {
            """
                实现多个组件在Spring环境下的功能测试，验证框架的功能：拦截器、AOP、日志、事务处理等
            """.trimIndent()
        }
    )
}
