package com.thoughtworks.projects.rental.onboarding.baseline

import com.thoughtworks.blackhorse.schema.story.StoryOf
import com.thoughtworks.blackhorse.schema.story.attributes.API
import com.thoughtworks.blackhorse.schema.story.attributes.HttpMethod
import com.thoughtworks.blackhorse.schema.story.attributes.HttpStatus
import com.thoughtworks.blackhorse.schema.story.expect
import com.thoughtworks.blackhorse.schema.story.given
import com.thoughtworks.blackhorse.schema.story.nested
import com.thoughtworks.blackhorse.schema.story.withApi
import com.thoughtworks.projects.rental.onboarding.architecture.application.PopularizationApplication.ApiController
import com.thoughtworks.projects.rental.onboarding.architecture.application.PopularizationApplication.Client
import com.thoughtworks.projects.rental.onboarding.architecture.application.PopularizationApplication.Interceptor
import com.thoughtworks.projects.rental.onboarding.architecture.application.PopularizationApplication.Service
import com.thoughtworks.projects.rental.onboarding.architecture.corebiz.PrepaidService
import com.thoughtworks.projects.rental.onboarding.architecture.middleware.MessageQueue
import com.thoughtworks.projects.rental.onboarding.team.Li4

object Baseline001 : StoryOf(
    title = "Baseline001",
    cardId = "Baseline-001",
    estimation = 8,
    configure = {
        inScope {
            """
            > **作为** 预充值用户，
            > **我想要** 将预充值账户中的月退回资金账户，
            > **以便于** 灵活使用个人或公司的资金
            
            预充值用户将对预充值的余额进行退款操作，在推广服务应用服务中为下游BFF服务提供退款API，并调用上游预充值服务完成退款功能
            """.trimIndent()
        }

        outOfScope {
            """
            只包括 推广服务应用服务 的API功能，相关的外部API将由其他卡来实现
            - 消费方：前端页面、BFF、预充值服务（回调）
            - 依赖方：
                - 预充值服务API
                    - 发起退款申请
                    - 消费消息队列中等待的退款申请
                    - 超过expire时间的处理
                - 消息队列API
                    - 发送退款消息
                    - 发送通知消息
            """.trimIndent()
        }

        val refundApi = API("退款API", HttpMethod.POST, "/prepaid/{account_id}/refund") {
            """
                {
                    "refund_amount": 100.00,
                    "currency": "CHN_YUAN",
                    "account": "WECHAT"
                }
            """.trimIndent()
        }

        ac {
            description {
                """
                   如果是非预充值用户，无法访问退款功能
                """.trimIndent()
            }
            note {
                """
                    通过解析Header.Authentication的JWT，调用鉴权认证服务进行身份验证；
                    Interceptor和鉴权认证服务已集成在框架中；
                """.trimIndent()
            }

            val unauthorizedFailure = refundApi.onFailed(HttpStatus.UNAUTHORIZED) {
                """
                    {
                        "msg": "未授权的操作，请重试"
                    }
                """.trimIndent()
            }

            flow("匿名用户访问退款API时，返回401和错误信息") {
                Interceptor call ApiController given {
                    """
                        创建RefundApi, 加上@Secured("ROLE_PREPAID")只允许经纪人访问；
                    """.trimIndent()
                } withApi unauthorizedFailure expect {
                    """
                        当作为未知用户调用RefundApi时，身份验证失败抛出异常并返回401；
                    """.trimIndent()
                }
            }

            flow("个人用户访问退款API时，返回401和错误信息") {
                Interceptor call ApiController withApi unauthorizedFailure expect {
                    """
                        当作为个人用户调用RefundApi时（@WithMockUser(roles = ["INDIVIDUAL"])），身份验证失败抛出异常并返回401；
                    """.trimIndent()
                }
            }

            flow("未开通预充值的经纪人用户访问退款API时，返回401和错误信息") {
                Interceptor call ApiController withApi unauthorizedFailure expect {
                    """
                        当作为个人用户调用RefundApi时（@WithMockUser(roles = ["OFFICER"])），身份验证失败抛出异常并返回401；
                    """.trimIndent()
                }
            }
        }

        val refundApiForPrepaid = API("退款API", HttpMethod.POST, "/prepaid/{account_id}/refund") {
            """
                {
                    "refund_amount": 100.00,
                    "currency": "CHN_YUAN",
                    "account": "WECHAT",
                    "requestor": "<user_id>"
                }
            """.trimIndent()
        }

        ac {
            description {
                """
                   如果是预充值用户，提交退款申请到预充值服务
                """.trimIndent()
            }

            flow("预充值用户访问退款API时，返回200") {
                Interceptor call ApiController withApi refundApi.onSuccess() expect {
                    """
                        当作为预充值用户调用RefundApi时（@WithMockUser(roles = ["PREPAID"])），返回200;
                    """.trimIndent()
                }
            }

            flow("调用退款API，提交申请到预充值服务") {
                ApiController call Service given {
                    """
                        获取请求参数和用户user_id，并调用RefundService执行退款操作
                    """.trimIndent()
                } nested {
                    Service call Client given {
                        """
                            调用PrepaidClient-预支付服务接口
                        """.trimIndent()
                    } nested {
                        Client call PrepaidService.ApiController withApi refundApiForPrepaid.onSuccess() expect {
                            """
                                成功调用预支付服务API，返回200；
                            """.trimIndent()
                        }
                    }
                }
            }
        }

        ac {
            description {
                """
                   如果是预充值用户，提交退款申请到预充值服务，返回余额不足退款失败，告知用户
                """.trimIndent()
            }

            flow("调用退款API，通过预充值服务进行退款时告知余额不足") {
                ApiController call Service nested {
                    Service call Client nested {
                        Client call PrepaidService.ApiController withApi refundApiForPrepaid.onFailed(HttpStatus.BAD_REQUEST) {
                            """
                                {
                                    "code": "LACK_OF_BALANCE",
                                    "msg": "退款失败，余额不足"
                                }
                            """.trimIndent()
                        } expect {
                            """
                                预支付服务退款API返回400错误；
                            """.trimIndent()
                        }
                    } expect {
                        """
                            Service会透传Client抛出的Exception
                        """.trimIndent()
                    }
                } expect {
                    """
                        捕获FeignClientException.BadRequest，获取错误码和信息并通过ResponseEntity返回
                    """.trimIndent()
                }
            }
        }

        val addRefundEventApi = API("提交待退款请求", HttpMethod.POST, "/events") {
            """
                {
                    "topic": "QUEUED_REFUND",
                    "payload": {
                        "refund_amount": 100.00,
                        "currency": "CHN_YUAN",
                        "account": "WECHAT",
                        "requestor": "<user_id>"
                    },
                    "createdAt": "<datetime_iso>"
                }
            """.trimIndent()
        }
        val addNotificationEventApi = API("提交通知消息", HttpMethod.POST, "/events") {
            """
                {
                    "topic": "NOTIFICATION",
                    "payload": {
                        "to": "<user_id>",
                        "title": "退款通知",
                        "msg": "..."
                    },
                    "createdAt": "<datetime_iso>"
                }
            """.trimIndent()
        }

        ac {
            description {
                """
                   如果是预充值用户，预充值服务不可用时，提交退款申请到消息队列进行缓存
                """.trimIndent()
            }

            flow("通过预充值服务进行退款时，预充值服务不可用，转发申请到消息队列") {
                Service call Client nested {
                    Client call PrepaidService.ApiController withApi refundApiForPrepaid.onFailed(HttpStatus.INTERNAL_SERVER_ERROR) expect {
                        """
                            预支付服务接口调用失败，返回5xx错误
                        """.trimIndent()
                    }
                } expect {
                    """
                        捕获FeignServerException，打印日志
                    """.trimIndent()
                }
                Service call Client given {
                    """
                        组装DTO，调用MqClient发布异步任务
                    """.trimIndent()
                } nested {
                    Client call MessageQueue.SQS withApi addRefundEventApi.onSuccess()
                } expect {
                    """
                        Service无异常抛出
                    """.trimIndent()
                }
            }
        }

        val confirmRefundAPI = API("退款结果更新", HttpMethod.POST, "/prepaid/{account_id}/refund/{rid}/confirmation") {
            """
                {
                    "refund_amount": 100.00,
                    "currency": "CHN_YUAN",
                    "account": "WECHAT",
                    "requestor": "<user_id>",
                    "status": "<status>",
                    "reason": "<reason>",
                    "updatedAt": "<datetime_iso>"
                }
            """.trimIndent()
        }

        ac {
            description {
                """
                   接收预充值服务回调：退款成功、失败，发送消息通知用户
                """.trimIndent()
            }

            flow("接收预充值服务的回调，更新退款信息并发送消息") {
                ApiController call Service given {
                    """
                        获取退款结果信息包装成VO
                    """.trimIndent()
                } withApi confirmRefundAPI.onSuccess() nested {
                    Service call Client given {
                        """
                            将退款信息VO序列化成通知消息，并通过Client进行发送
                            - `退款成功，<refund_amount>已退回到您的<account>账户。`
                            - `退款失败，<reason>。`
                        """.trimIndent()
                    } nested {
                        Client call MessageQueue.SQS withApi addNotificationEventApi.onSuccess()
                    }
                }
            }
        }
    },
    tracking = {
        decomposition(Li4, 240)
    }
)

fun main() {
    Baseline001.print()
}
