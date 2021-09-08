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

object `Story-1002` : StoryOf(
    title = "Story-1002",
    cardId = "Story-1002",
    estimation = 3,
    configure = {
        inScope {
            """
                作为 【平台运营】，我想要 【根据合作协议，扣除商家的余额入账】，以便于【作为处罚】

                业务规则：
                - 当余额扣减为0时，会扣减押金
                - 需要返回扣减结果给下游服务（"订单管理应用服务"），以便调用平台通知告知商家
                - 押金可为负数（押金<=0时会对商家进行停业整顿，并进行押金补交）
                - 扣减记录需要追溯（允许开具发票）

                涉及的数据结构：（可参考API Schema）
                - 商家账户包含（id，余额，押金）
                - 收入记录包含（商家id，订单id，收入金额，扣罚金额，扣罚原因）
            """.trimIndent()
        }

        outOfScope {
            """
                假设：此接口由下游服务 "订单管理应用服务" 在订单完成后自动调用，返回200时即为确认，返回500时下游服务会自动重试
                假设：押金<=0时会对商家进行停业整顿，由下游服务对交易系统进行操作控制
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
                Controller call Service withApi deductScenario given {
                    """
                        按示例组装ViewObject，mock Service正常执行
                        按示例发送Http请求，进行余额扣罚
                        调用成功，返回更新后的余额
                    """.trimIndent()
                } nested {
                    Service call RepositoryClient given {
                        """
                            按示例组装ViewObject
                                - mock Repository返回当前账户Entity（账户余额100）
                                - mock Repository保存当前账户Entity（账户余额0）
                                - mock Repository返回收入记录Entity（收入100，扣罚0）
                                - mock Repository保存收入记录Entity（收入100，扣罚100，原因投诉）
                            调用Service方法，进行余额扣罚
                            调用成功，返回更新后的余额（0）
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

            flow("当前商户id：10001，账户余额为100；订单id：aaaa-bbbb-cccc-dddd，处罚扣减90；更新后账户id：10001，账户余额为10，订单收入记录增加扣罚信息") {
                Service call RepositoryClient given {
                    """
                        按示例组装ViewObject
                            - mock Repository返回当前账户Entity（账户余额100）
                            - mock Repository保存当前账户Entity（账户余额0）
                            - mock Repository返回收入记录Entity（收入100，扣罚0）
                            - mock Repository保存收入记录Entity（收入100，扣罚100，原因投诉）
                        调用Service方法，进行余额扣罚
                        调用成功，返回更新后的余额（10）
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
                Controller call Service withApi deductScenario given {
                    """                        
                        按示例组装ViewObject，mock Service正常执行
                        按示例发送Http请求，进行余额扣罚
                        调用成功，返回更新后的余额和押金
                    """.trimIndent()
                } nested {
                    Service call RepositoryClient given {
                        """
                            按示例组装ViewObject
                                - mock Repository返回当前账户Entity（账户余额100，押金30000）
                                - mock Repository保存当前账户Entity（账户余额0，押金29990）
                                - mock Repository返回收入记录Entity（收入100，扣罚110）
                                - mock Repository保存收入记录Entity（收入100，扣罚110，原因投诉）
                            调用Service方法，进行余额扣罚
                            调用成功，返回更新后的余额和押金（0，29990）
                        """.trimIndent()
                    } nested {
                        RepositoryClient call DB given {
                            """
                                押金能随账户余额被更新，能够通过fake DB进行保存和查询
                                按示例组装收入记录信息Entity，能够通过fake DB进行保存和查询
                            """.trimIndent()
                        }
                    }
                }
            }

            flow("当前商户id：10001，账户余额为100，押金10；订单id：aaaa-bbbb-cccc-dddd，处罚扣减110；更新后账户id：10001，账户余额为0，押金0，订单收入记录增加扣罚信息") {
                Service call RepositoryClient given {
                    """
                        按示例组装ViewObject
                            - mock Repository返回当前账户Entity（账户余额100，押金10）
                            - mock Repository保存当前账户Entity（账户余额0，押金0）
                            - mock Repository返回收入记录Entity（收入100，扣罚110）
                            - mock Repository保存收入记录Entity（收入100，扣罚110，原因投诉）
                        调用Service方法，进行余额扣罚
                        调用成功，返回更新后的余额和押金（0，0）
                    """.trimIndent()
                }
            }

            flow("当前商户id：10001，账户余额为100，押金0；订单id：aaaa-bbbb-cccc-dddd，处罚扣减110；更新后账户id：10001，账户余额为0，押金-10，订单收入记录增加扣罚信息") {
                Service call RepositoryClient given {
                    """
                        按示例组装ViewObject
                            - mock Repository返回当前账户Entity（账户余额100，押金0）
                            - mock Repository保存当前账户Entity（账户余额0，押金-10）
                            - mock Repository返回收入记录Entity（收入100，扣罚110）
                            - mock Repository保存收入记录Entity（收入100，扣罚110，原因投诉）
                        调用Service方法，进行余额扣罚
                        调用成功，返回更新后的余额和押金（0，-10）
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
