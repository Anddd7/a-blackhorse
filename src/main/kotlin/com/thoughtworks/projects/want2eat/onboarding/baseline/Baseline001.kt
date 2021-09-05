package com.thoughtworks.projects.want2eat.onboarding.baseline

import com.thoughtworks.blackhorse.schema.story.StoryOf
import com.thoughtworks.blackhorse.schema.story.attributes.API
import com.thoughtworks.blackhorse.schema.story.attributes.HttpMethod
import com.thoughtworks.blackhorse.schema.story.attributes.HttpStatus
import com.thoughtworks.blackhorse.schema.story.expect
import com.thoughtworks.blackhorse.schema.story.given
import com.thoughtworks.blackhorse.schema.story.nested
import com.thoughtworks.blackhorse.schema.story.withApi
import com.thoughtworks.projects.want2eat.onboarding.architecture.MerchantService
import com.thoughtworks.projects.want2eat.onboarding.architecture.MerchantService.Controller

object Baseline001 : StoryOf(
    title = "Baseline001",
    cardId = "Baseline001",
    estimation = 5,
    configure = {
        inScope {
            """
            作为 【入驻商家】，我想要 【进行余额的提现】，以便于【将店铺运营的利润转化为实际的收入】
            
            提现条件：
            - 提现金额 < 当前余额
            - 每月仅能提现一次
            """.trimIndent()
        }

        outOfScope {
            """
            假设：所依赖的外部接口均已开发完成，直接调用即可
            假设：提现完成后会由消息队列发起回调，提示提现完成
            """.trimIndent()
        }

        val withdrawApi = API("提现API", HttpMethod.POST, "/merchant-account/balance/withdraw") {
            """
                {
                    "merchant_account_id": 100001,
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
                        "merchant_account_id": 100001,
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
                    当本月第一次提现，且提现金额小于当前余额时，提现成功
                """.trimIndent()
            }

            flow("当前余额100，提现100，当月无提现记录") {
                Controller call MerchantService.Service withApi withdrawApi.onSuccess() given {
                    """
                        获取请求参数`merchant_account_id, amount`，调用mock Service
                    """.trimIndent()
                } nested {
                    MerchantService.Service call MerchantService.Repository given {
                        """
                            通过`merchant_account_id`查询当前余额，调用mock Repository返回商家账户信息
                            通过`merchant_account_id, time.now()`查询当月提现记录，调用mock Repository返回提现记录
                            新建一条提现记录
                        """.trimIndent()
                    } expect {
                        """
                            mock Repository返回商家账户信息 - 余额100
                            mock Repository返回提现记录 - 0条
                            mock Repository创建提现记录
                        """.trimIndent()
                    } nested {
                        MerchantService.Repository call MerchantService.DB given {
                            """
                                创建JPA方法，调用fake DB预插入一条商户账户数据 - 余额100
                                创建JPA方法，调用fake DB预插入一条提现记录数据 - 上月提现记录
                            """.trimIndent()
                        }
                    }

                    MerchantService.Service call MerchantService.Client given {
                        """
                            创建一条提现申请消息，调用mock Client进行发送
                        """.trimIndent()
                    } nested {
                        MerchantService.Client call MerchantService.MQ withApi withdrawMsgApi.onSuccess() expect {
                            """
                                mock MQ收到了的消息请求中的余额 = 100
                            """.trimIndent()
                        }
                    }
                }
            }
        }

        ac {
            description {
                """
                    当提现金额大于当前余额时，提现失败
                """.trimIndent()
            }

            flow("当前余额99，提现金额100") {
                Controller call MerchantService.Service withApi withdrawApi.onFailed(HttpStatus.BAD_REQUEST) {
                    """
                        {
                            "message": "balance insufficient"
                        }
                    """.trimIndent()
                } given {
                    """
                        获取请求参数`merchant_account_id, amount`，调用mock Service
                    """.trimIndent()
                } expect {
                    """
                        mock Service抛出异常
                    """.trimIndent()
                } nested {
                    MerchantService.Service call MerchantService.Repository given {
                        """
                            通过`merchant_account_id`查询当前余额，调用mock Repository返回商家账户信息
                        """.trimIndent()
                    } expect {
                        """
                            mock Repository返回商家账户信息 - 余额99
                            抛出`余额不足`的业务异常
                        """.trimIndent()
                    }
                }
            }
        }

        ac {
            description {
                """
                    当本月已有提现记录时，提现失败
                """.trimIndent()
            }

            flow("当前余额100，提现100，但当月已有一笔提现记录") {
                Controller call MerchantService.Service withApi withdrawApi.onFailed(HttpStatus.BAD_REQUEST) {
                    """
                        {
                            "message": "withdraw exists"
                        }
                    """.trimIndent()
                } given {
                    """
                        获取请求参数`merchant_account_id, amount`，调用mock Service
                    """.trimIndent()
                } expect {
                    """
                        mock Service抛出异常
                    """.trimIndent()
                } nested {
                    MerchantService.Service call MerchantService.Repository given {
                        """
                            通过`merchant_account_id, time.now()`查询当月提现记录，调用mock Repository返回提现记录
                        """.trimIndent()
                    } expect {
                        """
                            mock Repository返回提现记录 - 1条
                            抛出`已提现`的业务异常
                        """.trimIndent()
                    }
                }
            }
        }

        val withdrawCallbackApi =
            API("提现回调API", HttpMethod.POST, "/merchant-account/balance/withdraw/{id}/confirmation") {
                """
                {
                    "merchant_account_id": 100001,
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
            flow("当前余额100，提现100，但当月已有一笔提现记录") {
                Controller call MerchantService.Service withApi withdrawCallbackApi.onSuccess() given {
                    """
                        获取请求参数`withdraw_id, updated_at`, 调用mock Service
                    """.trimIndent()
                } nested {
                    MerchantService.Service call MerchantService.Repository given {
                        """
                            通过`withdraw_id`查询已有提现记录，更新完成状态和完成时间
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
