package com.thoughtworks.projects.want2eat.onboarding

import com.thoughtworks.blackhorse.schema.story.StoryOf
import com.thoughtworks.blackhorse.schema.story.attributes.API
import com.thoughtworks.blackhorse.schema.story.attributes.HttpMethod
import com.thoughtworks.blackhorse.schema.story.given
import com.thoughtworks.blackhorse.schema.story.nested
import com.thoughtworks.blackhorse.schema.story.withApi
import com.thoughtworks.projects.want2eat.onboarding.architecture.MerchantService

object `Story-1001` : StoryOf(
    title = "Story-1001",
    cardId = "Story-1001",
    estimation = 3,
    configure = {
        inScope {
            """
                作为 【入驻商家】，我想要 【完成订单后获得相应的余额收入】，以便于【查询余额和提现】
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
                MerchantService.Controller call MerchantService.Service withApi incomeApi.onSuccess() given {
                    """
                        获取请求参数组装ViewObject，调用mock Service
                    """.trimIndent()
                } nested {
                    MerchantService.Service call MerchantService.Repository given {
                        """
                            组装Entity，调用mock Repository创建一条收入记录
                            更新商户账户的余额为100，调用mock Repository进行保存
                        """.trimIndent()
                    } nested {
                        MerchantService.Repository call MerchantService.DB given {
                            """
                                测试Repository能够使用Entity操作fake 数据库并执行对应的SQL语句
                            """.trimIndent()
                        }
                    }
                }
            }

            flow("当前商户id：10001，账户余额100；收到订单id：aaaa-bbbb-cccc-dddd，获得收入100；更新后账户id：10001，账户余额为200，新增收入记录，关联订单id：aaaa-bbbb-cccc-dddd") {
                MerchantService.Service call MerchantService.Repository given {
                    """
                        组装Entity，调用mock Repository创建一条收入记录
                        更新商户账户的余额为200，调用mock Repository进行保存
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
