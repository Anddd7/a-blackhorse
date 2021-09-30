package com.thoughtworks.projects.rental.onboarding.architecture.application

import com.thoughtworks.blackhorse.schema.architecture.Component
import com.thoughtworks.blackhorse.schema.architecture.Container
import com.thoughtworks.blackhorse.schema.architecture.ProcessDefBuilder
import com.thoughtworks.blackhorse.schema.architecture.at
import com.thoughtworks.blackhorse.schema.architecture.attributes.Responsibility
import com.thoughtworks.blackhorse.schema.architecture.attributes.TechStack
import com.thoughtworks.blackhorse.schema.architecture.cost
import com.thoughtworks.blackhorse.schema.architecture.with
import com.thoughtworks.projects.rental.onboarding.architecture.CommonComponentLayer
import com.thoughtworks.projects.rental.onboarding.architecture.corebiz.PrepaidService
import com.thoughtworks.projects.rental.onboarding.architecture.middleware.MessageQueue
import com.thoughtworks.projects.rental.onboarding.team.Li4

/**
 * @see PopularizationApplication
 */
object PopularizationApplication : Container(
    // 进程编号 —— 用于区分各进程的工序
    id = "32",
    // 进程间分层 —— 按职责划分，可自定义
    layer = Responsibility.ApplicationService,
    // 技术栈描述
    techStack = listOf(
        TechStack("Spring", "使用Spring生态搭建后端服务"),
        TechStack("Kotlin", "vs Java，语法简洁、并发支持更好"),
    ),
    // 进程职责
    responsibility = "推广服务应用服务: 向前端服务/应用提供推广服务和与充值服务相关的功能接口",
    // 负责人（背锅侠）
    owner = listOf(Li4)
) {
    // 进程内组件
    object Interceptor : Component(
        // 进程内分层
        layer = CommonComponentLayer.Authentication,
        // 技术栈描述
        techStack = listOf(
            TechStack("Spring Servlet"),
        ),
        // 组件职责
        responsibility = "拦截请求并验证发起方身份"
    )

    object ApiController : Component(
        CommonComponentLayer.Controller,
        listOf(
            TechStack("Spring Mvc"),
        ),
        "定义API、校验请求数据，调用Service完成业务逻辑"
    )

    object Service : Component(
        CommonComponentLayer.Service,
        listOf(
            TechStack("POJO"),
        ),
        "业务逻辑处理"
    )

    object VO : Component(
        CommonComponentLayer.Service,
        listOf(
            TechStack("Kotlin"),
        ),
        "数据模型对象"
    )

    object Client : Component(
        CommonComponentLayer.Client,
        listOf(
            TechStack("RestTemplate"),
        ),
        "HttpClient消费下游服务API"
    )

    object DTO : Component(
        CommonComponentLayer.Client,
        listOf(
            TechStack("POJO"),
        ),
        "下游服务数据模型对象"
    )

    override fun getProcesses(): List<ProcessDefBuilder> = listOf(
        Interceptor stub ApiController cost 10 at {
            """
                Interceptor和Controller依赖于Spring，需要启动整个容器参与测试；
                ```
                // Q1 组件测试，基于Spring Security Test
                ```
            """.trimIndent()
        },
        ApiController mock Service at {
            """
                保证Controller配置了正确的API，接收请求调用Service并返回正确的Json数据；
                ```
                // Q1 组件测试，基于WebMvcTest
                ```
            """.trimIndent()
        },
        Service mock Service at {
            """
                需要保证对Service调用的入参和返回都正确；
                 ```
                // Q1 单元测试
                ```
            """.trimIndent()
        },
        Service mock Client at {
            """
                需要保证对Client调用的入参和返回都正确；
                 ```
                // Q1 单元测试
                ```
            """.trimIndent()
        },
        Client spy MessageQueue.SQS with "Wiremock" at {
            """
                处于进程边界，需要真实的Http调用；发送消息是异步操作只需要保证入参正确即可；
                 ```
                // Q1 单元测试
                ```
            """.trimIndent()
        },
        Client mock PrepaidService.ApiController with "Wiremock" at {
            """
                处于进程边界，需要真实的Http调用；需要保证对外调用的入参和返回都正确；
                 ```
                // Q1 单元测试
                ```
            """.trimIndent()
        },
    )
}
