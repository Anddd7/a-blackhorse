package com.thoughtworks.projects.want2eat.onboarding

import com.thoughtworks.blackhorse.schema.story.StoryOf
import com.thoughtworks.blackhorse.schema.story.attributes.API
import com.thoughtworks.blackhorse.schema.story.attributes.HttpMethod
import com.thoughtworks.blackhorse.schema.story.given
import com.thoughtworks.blackhorse.schema.story.nested
import com.thoughtworks.blackhorse.schema.story.withApi
import com.thoughtworks.projects.want2eat.onboarding.architecture.MerchantService

object `Story-1002` : StoryOf(
    title = "Story-1002",
    cardId = "Story-1002",
    estimation = 3,
    configure = {
        inScope {
            """
               作为 【平台运营】，我想要 【根据合作协议，扣除商家的余额入账】，以便于【作为处罚】
               
               注意事项：
               - 当余额扣减为0时，会扣减押金
               - 需要返回扣减结果给下游服务（"订单管理应用服务"），以便调用平台通知告知商家
               - 押金可为负数（押金<=0时会对商家进行停业整顿，并进行押金补交）
               - 扣减记录需要追溯（允许开具发票）
            """.trimIndent()
        }

        outOfScope {
            """
                假设：此接口由下游服务 "订单管理应用服务" 在订单完成后自动调用，返回200时即为确认，返回500时下游服务会自动重试
            """.trimIndent()
        }

        val deductApi = API("订单处罚API", HttpMethod.POST, "/merchant-account/balance/deduct") {
            """
                {
                    "merchant_account_id": 100001,
                    "order_id": "<uuid>",
                    "deduct_amount": 100
                }
            """.trimIndent()
        }

        val deductScenario = deductApi.onSuccess {
            """
                {
                    "balance": 100,
                    "deposit": 100
                }
            """.trimIndent()
        }

        ac {
            description {
                """
                    收到处罚信息，余额扣减
                """.trimIndent()
            }

            flow("当前商户id：10001，账户余额为100；订单id：aaaa-bbbb-cccc-dddd，处罚扣减100；更新后账户id：10001，账户余额为0，订单收入记录增加扣罚信息") {
                MerchantService.Controller call MerchantService.Service withApi deductScenario given {
                    """
                        获取请求参数组装ViewObject，调用mock Service
                    """.trimIndent()
                } nested {
                    MerchantService.Service call MerchantService.Repository given {
                        """
                            通过"order_id"，调用mock Repository查询已有收入记录
                            更新处罚金额和处罚原因，调用mock Repository查询保存收入记录
                            扣减账户余额100，调用mock Repository进行更新
                        """.trimIndent()
                    } nested {
                        MerchantService.Repository call MerchantService.DB given {
                            """
                                测试Repository能够使用Entity操作数据库并执行对应的SQL语句
                            """.trimIndent()
                        }
                    }
                }
            }

            flow("当前商户id：10001，账户余额为100；订单id：aaaa-bbbb-cccc-dddd，处罚扣减90；更新后账户id：10001，账户余额为10，订单收入记录增加扣罚信息") {
                MerchantService.Service call MerchantService.Repository given {
                    """
                        通过"order_id"，调用mock Repository查询已有收入记录
                        更新处罚金额和处罚原因，调用mock Repository查询保存收入记录
                        扣减账户余额90，调用mock Repository进行更新
                    """.trimIndent()
                }
            }
        }

        ac {
            description {
                """
                    收到处罚信息，余额为0时，扣减
                """.trimIndent()
            }

            flow("当前商户id：10001，账户余额为100，押金30000；订单id：aaaa-bbbb-cccc-dddd，处罚扣减110；更新后账户id：10001，账户余额为0，押金29990，订单收入记录增加扣罚信息") {
                MerchantService.Controller call MerchantService.Service withApi deductScenario given {
                    """                        
                        获取请求参数组装ViewObject，调用mock Service
                    """.trimIndent()
                } nested {
                    MerchantService.Service call MerchantService.Repository given {
                        """
                            通过"order_id"，调用mock Repository查询已有收入记录
                            更新处罚金额和处罚原因，调用mock Repository查询保存收入记录
                            通过"merchant_account_id"，调用mock Repository查询账户信息
                            扣减账户余额100，调用mock Repository进行更新
                            扣减账户押金10，调用mock Repository进行更新
                        """.trimIndent()
                    } nested {
                        MerchantService.Repository call MerchantService.DB given {
                            """
                                测试Repository能够使用Entity操作数据库并执行对应的SQL语句
                            """.trimIndent()
                        }
                    }
                }
            }

            flow("当前商户id：10001，账户余额为100，押金10；订单id：aaaa-bbbb-cccc-dddd，处罚扣减110；更新后账户id：10001，账户余额为0，押金0，订单收入记录增加扣罚信息") {
                MerchantService.Service call MerchantService.Repository given {
                    """
                        通过"order_id"，调用mock Repository查询已有收入记录
                        更新处罚金额和处罚原因，调用mock Repository查询保存收入记录
                        通过"merchant_account_id"，调用mock Repository查询账户信息
                        扣减账户余额100，调用mock Repository进行更新
                        扣减账户押金10，调用mock Repository进行更新
                    """.trimIndent()
                }
            }

            flow("当前商户id：10001，账户余额为100，押金0；订单id：aaaa-bbbb-cccc-dddd，处罚扣减110；更新后账户id：10001，账户余额为0，押金-10，订单收入记录增加扣罚信息") {
                MerchantService.Service call MerchantService.Repository given {
                    """
                        通过"order_id"，调用mock Repository查询已有收入记录
                        更新处罚金额和处罚原因，调用mock Repository查询保存收入记录
                        通过"merchant_account_id"，调用mock Repository查询账户信息
                        扣减账户余额100，调用mock Repository进行更新
                        扣减账户押金10，调用mock Repository进行更新
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
    `Story-1002`.print()
}
