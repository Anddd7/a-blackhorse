package com.thoughtworks.projects.want2eat.onboarding.baseline

import com.thoughtworks.blackhorse.schema.story.StoryOf
import com.thoughtworks.blackhorse.schema.story.attributes.API
import com.thoughtworks.blackhorse.schema.story.attributes.HttpMethod
import com.thoughtworks.blackhorse.schema.story.attributes.HttpStatus
import com.thoughtworks.blackhorse.schema.story.given
import com.thoughtworks.blackhorse.schema.story.nested
import com.thoughtworks.blackhorse.schema.story.withApi
import com.thoughtworks.projects.want2eat.onboarding.architecture.MerchantService.Controller
import com.thoughtworks.projects.want2eat.onboarding.architecture.MerchantService.DB
import com.thoughtworks.projects.want2eat.onboarding.architecture.MerchantService.MQGateway
import com.thoughtworks.projects.want2eat.onboarding.architecture.MerchantService.RepositoryClient
import com.thoughtworks.projects.want2eat.onboarding.architecture.MerchantService.Service
import com.thoughtworks.projects.want2eat.onboarding.architecture.MerchantService.SpringBootTest

object Baseline001 : StoryOf(
    title = "Baseline001",
    cardId = "Baseline001",
    estimation = 5,
    configure = {
        inScope {
            """
            作为 【入驻商家】，我想要 【进行余额的提现】，以便于【将店铺运营的利润转化为实际的收入】
            
            业务规则：
            - 提现金额 < 当前余额
            - 当前余额可能会因为投诉而被扣减，需要考虑并发问题
            
            涉及的数据结构：（可参考API Schema）
            - 商家账户包含（id，余额）
            - 提现记录包含（id，商家id，提现金额，单位，提现渠道，提现状态，更新时间）
            - 提现消息包含（主题，回调url，消息内容）
            """.trimIndent()
        }

        outOfScope {
            """
            假设：所依赖的外部接口均已开发完成，直接调用即可
            假设：提现完成后会由消息队列发起回调，提示提现完成
            """.trimIndent()
        }

        val withdrawApi = API("商户提现API", HttpMethod.POST, "/merchant-account/balance/withdraw") {
            """
                {
                    "merchant_account_id": 10001,
                    "amount": 100.00,
                    "currency": "CHN_YUAN",
                    "channel": "WECHAT"
                }
            """.trimIndent()
        }

        val withdrawMsgApi = API("提现申请消息API", HttpMethod.POST, "/messages") {
            """
                {
                    "topic": "merchant_account_balance_withdraw",
                    "callback": "/merchant-account/balance/withdraw/{id}/confirmation"
                    "payload": {
                        "merchant_account_id": 10001,
                        "amount": 100.00,
                        "currency": "CHN_YUAN",
                        "channel": "WECHAT"
                    }
                }
            """.trimIndent()
        }

        ac {
            description {
                """
                    当提现金额小于或等于当前余额时，提现成功
                """.trimIndent()
            }

            flow("当前id：10001，账户余额100；提现100；提现成功后账户id：10001，账户余额为0") {
                Controller call Service withApi withdrawApi.onSuccess() given {
                    """
                        按示例组装ViewObject，mock Service正常执行
                        按示例发送Http请求，进行提现
                        调用成功，无返回值
                    """.trimIndent()
                } nested {
                    Service call RepositoryClient given {
                        """
                            按示例组装ViewObject
                                - mock Repository返回当前账户Entity（账户余额100）
                                - mock Repository保存当前账户Entity（账户余额0）
                                - mock Repository保存提现记录Entity（状态"处理中"）
                                - mock Client发送提现消息DTO（包含消息主体，消息回调地址和消息体信息)
                            调用Service方法，进行提现
                            调用成功，无返回值
                        """.trimIndent()
                    } nested {
                        RepositoryClient call DB given {
                            """
                                按示例组装账户信息Entity，能够通过fake DB进行保存和查询
                                按示例组装提现信息Entity，能够通过fake DB进行保存和查询
                            """.trimIndent()
                        }
                        RepositoryClient call MQGateway withApi withdrawMsgApi.onSuccess() given {
                            """
                                按示例组装消息DTO，能够与fake Client进行交互
                            """.trimIndent()
                        }
                    }
                }
            }

            flow("当前id：10001，账户余额100；提现99；提现成功后账户id：10001，账户余额为1") {
                Service call RepositoryClient given {
                    """
                        按示例组装ViewObject
                            - mock Repository返回当前账户Entity（账户余额100）
                            - mock Repository保存当前账户Entity（账户余额99）
                            - mock Repository保存提现记录Entity（状态"处理中"）
                            - mock Client发送提现消息DTO（包含消息主体，消息回调地址和消息体信息)
                        调用Service方法，进行提现
                        调用成功，无返回值
                    """.trimIndent()
                }
            }
        }

        ac {
            description {
                """
                    当提现金额大于当前余额时，提现失败
                """.trimIndent()
            }

            flow("当前id：10001，账户余额100；提现101；提现失败返回错误信息") {
                Controller call Service withApi withdrawApi.onFailed(HttpStatus.BAD_REQUEST) {
                    """
                        {
                            "message": "balance insufficient"
                        }
                    """.trimIndent()
                } given {
                    """
                        按示例组装ViewObject，mock Service抛出余额不足异常
                        按示例发送Http请求，进行提现
                        调用失败，返回错误信息
                    """.trimIndent()
                } nested {
                    Service call RepositoryClient given {
                        """
                            按示例组装ViewObject
                                - mock Repository返回当前账户Entity（账户余额100）
                            调用Service方法，进行提现
                            调用失败，抛出余额不足异常
                        """.trimIndent()
                    }
                }
            }

            flow("当前id：10001，账户余额100；提现100 * 100次；仅生成一次提现记录：处理中，金额100") {
                Service call RepositoryClient given {
                    """
                        按示例组装ViewObject
                            - mock Repository返回当前账户Entity（账户余额100）
                            - mock Repository直接更新账户余额数据（更新成功返回1，更新失败返回0）
                            - mock Repository保存提现记录Entity（状态"处理中"）
                            - mock Client发送提现消息DTO（包含消息主体，消息回调地址和消息体信息)
                        调用Service方法，进行提现
                        调用成功，无返回值
                    """.trimIndent()
                } nested {
                    RepositoryClient call DB given {
                        """
                            mock Repository已存在账户Entity（账户余额100）
                            调用update sql方法更新余额数据
                            更新成功返回1，更新失败返回0
                        """.trimIndent()
                    }
                }

                SpringBootTest call SpringBootTest given {
                    """
                        按示例组装ViewObject
                        按示例发送Http请求，并发执行100次，进行提现
                        调用完成后监测数据库，仅有一条提现记录生成
                    """.trimIndent()
                }
            }
        }

        val withdrawCallbackApi =
            API("提现成功回调API", HttpMethod.POST, "/merchant-account/balance/withdraw/{id}/confirmation") {
                """
                {
                    "merchant_account_id": 10001,
                    "updated_at": "<timestamp_iso>"
                }
                """.trimIndent()
            }

        ac {
            description {
                """
                    当提现完成时，标记提现数据
                """.trimIndent()
            }
            flow("当前提现请求id：1000000，提现状态为处理中；更新后提现记录id：1000000，状态为'已完成'") {
                Controller call Service withApi withdrawCallbackApi.onSuccess() given {
                    """
                        按示例组装ViewObject，mock Service正常执行
                        按示例发送Http请求，完成提现请求的确认
                        调用成功，无返回值
                    """.trimIndent()
                } nested {
                    Service call RepositoryClient given {
                        """
                            按示例传递请求参数
                                - mock Repository获取提现记录Entity（状态"处理中"）
                                - mock Repository保存更新后的提现记录Entity（状态"完成"）
                            调用Service方法，进行提现完成的确认
                            调用成功，无返回值
                        """.trimIndent()
                    }
                }
            }
        }
    },
    tracking = {
        decomposition("adliao", 80)
    }
)

fun main() {
    Baseline001.print()
}
