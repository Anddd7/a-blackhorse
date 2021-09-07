package com.thoughtworks.projects.want2eat.onboarding.baseline

import com.thoughtworks.blackhorse.schema.story.StoryOf
import com.thoughtworks.blackhorse.schema.story.attributes.API
import com.thoughtworks.blackhorse.schema.story.attributes.HttpMethod
import com.thoughtworks.blackhorse.schema.story.attributes.HttpStatus
import com.thoughtworks.blackhorse.schema.story.given
import com.thoughtworks.blackhorse.schema.story.nested
import com.thoughtworks.blackhorse.schema.story.withApi
import com.thoughtworks.projects.want2eat.onboarding.architecture.MerchantService.Client
import com.thoughtworks.projects.want2eat.onboarding.architecture.MerchantService.Controller
import com.thoughtworks.projects.want2eat.onboarding.architecture.MerchantService.DB
import com.thoughtworks.projects.want2eat.onboarding.architecture.MerchantService.MQ
import com.thoughtworks.projects.want2eat.onboarding.architecture.MerchantService.Repository
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
            
            Notes：
            - 提现金额 < 当前余额
            - 当前余额可能会因为投诉而被扣减，需要考虑并发问题
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
                    "channel": "WECHAT",
                    "expired_at": "<local date time>"
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
                        "channel": "WECHAT",
                        "expired_at": "<local date time>"
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
                        获取请求参数组装ViewObject，调用mock Service
                    """.trimIndent()
                } nested {
                    Service call Repository given {
                        """
                            通过`merchant_account_id`，调用mock Repository查询当前账户余额 - 当前余额100
                            组装Entity，调用mock Repository创建一条"处理中"的提现记录
                            扣减账户余额为0，调用mock Repository进行保存
                        """.trimIndent()
                    } nested {
                        Repository call DB given {
                            """
                                测试Repository能够使用Entity操作fake 数据库并执行对应的SQL语句
                            """.trimIndent()
                        }
                    }

                    Service call Client given {
                        """
                            组装消息Dto，调用mock Client进行发送一条提现申请消息
                        """.trimIndent()
                    } nested {
                        Client call MQ withApi withdrawMsgApi.onSuccess() given {
                            """
                                验证mock MQ收到了正确的消息请求
                            """.trimIndent()
                        }
                    }
                }
            }

            flow("当前id：10001，账户余额100；提现99；提现成功后账户id：10001，账户余额为1") {
                Service call Repository given {
                    """
                        通过`merchant_account_id`，调用mock Repository查询当前账户余额 - 当前余额100
                        组装Entity，调用mock Repository创建一条"处理中"的提现记录
                        扣减账户余额为1，调用mock Repository进行保存
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
                        获取请求参数组装ViewObject，调用mock Service
                    """.trimIndent()
                } nested {
                    Service call Repository given {
                        """
                            通过`merchant_account_id`查询当前余额，调用mock Repository返回商家账户信息 - 余额100
                            抛出`余额不足`的业务异常
                        """.trimIndent()
                    }
                }
            }

            flow("当前id：10001，账户余额100；提现100 * 100次；仅生成一次提现记录：处理中，金额100") {
                Service call Repository given {
                    """
                        增加事务注解，调用mock Repository进行余额扣减
                        扣减失败（更新失败）时，抛出`余额不足`的业务异常
                    """.trimIndent()
                } nested {
                    Repository call DB given {
                        """
                            使用update set进行数据库扣减，测试Repository能够执行对应的SQL语句
                        """.trimIndent()
                    }
                }

                SpringBootTest call SpringBootTest given {
                    """
                        提现100，同时执行100次；查询剩余余额为0，提现记录仅1条
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
                        获取请求参数组装ViewObject，调用mock Service更新完成状态和完成时间
                    """.trimIndent()
                } nested {
                    Service call Repository given {
                        """
                            通过`withdraw_id`，调用mock Repository查询已有提现记录
                            更新完成状态和完成时间并保存
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
