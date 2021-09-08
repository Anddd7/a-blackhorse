package com.thoughtworks.projects.want2eat.onboarding

import com.thoughtworks.blackhorse.schema.story.StoryOf
import com.thoughtworks.blackhorse.schema.story.attributes.API
import com.thoughtworks.blackhorse.schema.story.attributes.HttpMethod
import com.thoughtworks.blackhorse.schema.story.given
import com.thoughtworks.blackhorse.schema.story.nested
import com.thoughtworks.blackhorse.schema.story.withApi
import com.thoughtworks.projects.want2eat.onboarding.architecture.MerchantService.Controller
import com.thoughtworks.projects.want2eat.onboarding.architecture.MerchantService.DB
import com.thoughtworks.projects.want2eat.onboarding.architecture.MerchantService.RepositoryClient
import com.thoughtworks.projects.want2eat.onboarding.architecture.MerchantService.Service

object `Story-1001` : StoryOf(
    title = "Story-1001",
    cardId = "Story-1001",
    estimation = 3,
    configure = {
        inScope {
            """
                作为 【入驻商家】，我想要 【完成订单后获得相应的余额收入】，以便于【查询余额和提现】
                
                涉及的数据结构：（可参考API Schema）
                - 商家账户包含（id，余额）
                - 收入记录包含（商家id，订单id，收入金额）
            """.trimIndent()
        }

        outOfScope {
            """
                假设：此接口由下游服务 "订单管理应用服务" 在订单完成后自动调用，返回200时即为确认，返回500时下游服务会自动重试
            """.trimIndent()
        }

        val incomeApi = API("订单收入API", HttpMethod.POST, "/merchant-account/balance/income") {
            """
                {
                    "merchant_account_id": 100001,
                    "order_id": "<uuid>",
                    "amount": 100
                }
            """.trimIndent()
        }

        ac {
            description {
                """
                    获得订单收入，余额增加
                """.trimIndent()
            }

            flow("当前商户id：10001，账户余额0；收到订单id：aaaa-bbbb-cccc-dddd，获得收入100；更新后账户id：10001，账户余额为100，新增收入记录，关联订单id：aaaa-bbbb-cccc-dddd") {
                Controller call Service withApi incomeApi.onSuccess() given {
                    """
                        按示例组装ViewObject，mock Service正常执行
                        按示例发送Http请求，进行订单收入入账
                        调用成功，无返回值
                    """.trimIndent()
                } nested {
                    Service call RepositoryClient given {
                        """
                            按示例组装ViewObject
                                - mock Repository返回当前账户Entity（账户余额0）
                                - mock Repository保存当前账户Entity（账户余额100）
                                - mock Repository保存收入记录Entity
                            调用Service方法，进行订单收入入账
                            调用成功，无返回值
                        """.trimIndent()
                    } nested {
                        RepositoryClient call DB given {
                            """
                                按示例组装收入记录信息Entity，能够通过fake DB进行保存和查询
                            """.trimIndent()
                        }
                    }
                }
            }

            flow("当前商户id：10001，账户余额100；收到订单id：aaaa-bbbb-cccc-dddd，获得收入100；更新后账户id：10001，账户余额为200，新增收入记录，关联订单id：aaaa-bbbb-cccc-dddd") {
                Service call RepositoryClient given {
                    """
                        按示例组装ViewObject
                            - mock Repository返回当前账户Entity（账户余额100）
                            - mock Repository保存当前账户Entity（账户余额200）
                            - mock Repository保存收入记录Entity
                        调用Service方法，进行订单收入入账
                        调用成功，无返回值
                    """.trimIndent()
                }
            }
        }
    },
    tracking = {
        decomposition("adliao", 80)
    }
)

fun main() {
    `Story-1001`.print()
}
