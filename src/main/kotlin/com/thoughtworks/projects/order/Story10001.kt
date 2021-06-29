package com.thoughtworks.projects.order

import com.thoughtworks.blackhorse.schema.performance.BlockType
import com.thoughtworks.blackhorse.schema.performance.CardType
import com.thoughtworks.blackhorse.schema.performance.blocked
import com.thoughtworks.blackhorse.schema.performance.by
import com.thoughtworks.blackhorse.schema.performance.cause
import com.thoughtworks.blackhorse.schema.performance.cost
import com.thoughtworks.blackhorse.schema.story.API
import com.thoughtworks.blackhorse.schema.story.Estimation
import com.thoughtworks.blackhorse.schema.story.HttpMethod
import com.thoughtworks.blackhorse.schema.story.HttpStatus
import com.thoughtworks.blackhorse.schema.story.StoryOf
import com.thoughtworks.blackhorse.schema.story.accept
import com.thoughtworks.blackhorse.schema.story.print
import com.thoughtworks.blackhorse.schema.story.reply
import com.thoughtworks.blackhorse.schema.story.then
import com.thoughtworks.blackhorse.schema.story.withApi

object Story10001 : StoryOf(
    title = "Get the shopping cart info",
    estimation = Estimation.EIGHT_8,
    cardId = "STORY-10001",
    cardType = CardType.STORY,
    configure = {
        inScope {
            """
            get current shopping cart from backend and display shopping cart info: price, amount for each product, total of the products
            """
        }

        outOfScope {
            """
            - product info is getting from the external system
            """
        }

        val getShoppingCartFromBffApi = API("Get ShoppingCart", HttpMethod.GET, "/shoppingCart")
        val getShoppingCartApi = API("Get ShoppingCart", HttpMethod.GET, "/shoppingCart")

        ac {
            description {
                """
                when i am a customer, 
                i can see a message saying 'Your shopping cart is empty' when i haven't add any products, 
                so that i can add more products
                """
            }
            example {
                """
                William is reviewing his shopping cart without adding any product
                """
            }

            mockup("https://preview.redd.it/h73wnfhvk3l61.png?width=575&format=png&auto=webp&s=a43c711e25606d31d80ccc5bbdf4b42111abce2c")

            flow("render empty shopping cart") {
                Web.UiComponent call Web.UiComponent accept {
                    """
                        add 'ShoppingCart' page
                        add 'shopping cart' icon in menu which can redirect user to 'Shopping Cart' page
                        click 'shopping cart' and entering the 'Shopping Cart' page
                        
                        ```typescript
                        interface ShoppingCartProps {
                            items: ProductDto[]
                        }
                        ```
                    """.trimIndent()
                }
                Web.UiComponent call Web.AxiosClient accept "call the api" reply "return empty object"
                Web.UiComponent call Web.UiComponent accept "display message 'Your shopping cart is empty!'"
            }

            flow("call bff api") {
                Web.AxiosClient call Bff.Controller withApi getShoppingCartFromBffApi.onFailed(HttpStatus.NOT_FOUND)
            }

            flow("call service to get dto") {
                Bff.Controller call Bff.Service accept {
                    "retrieve user id from authentication header"
                } reply {
                    "throw not found exception and respond with 404"
                }
            }

            flow("call feign client to get dto") {
                Bff.Service call Bff.FeignClient accept "call feign client with user id" reply "throw not found exception"
            }

            flow("call backend to get dto") {
                Bff.FeignClient call Backend.Controller withApi getShoppingCartApi.onFailed(HttpStatus.NOT_FOUND)
            }

            flow("call usecase") {
                Backend.Controller call Backend.Usecase accept {
                    "call usecase to find the shopping cart by user id"
                } reply {
                    "throw not found exception and respond with 404"
                }
            }

            flow("call domain service") {
                Backend.Usecase call Backend.DomainService
            }

            flow("call repository") {
                Backend.DomainService call Backend.DomainRepository
            }

            flow("implement repository and inject the implementation") {
                Backend.RepositoryImpl call Backend.JpaDao accept {
                    "implement domain repository and search shopping cart in db"
                } reply "returns null"
            }

            flow("verify the sql") {
                Backend.JpaDao call Backend.Postgres
            }
        }

        ac {
            description {
                """
                when i am a customer,
                i can see my shopping cart with the products that i added before,
                so that i can review the amount and total price of them
                """
            }
            example {
                """
                William is reviewing his shopping cart after added some products
                """
            }
            mockup("https://preview.redd.it/h73wnfhvk3l61.png?width=575&format=png&auto=webp&s=a43c711e25606d31d80ccc5bbdf4b42111abce2c")

            flow("render shopping cart") {
                Web.UiComponent call Web.AxiosClient accept "click 'the shopping cart' icon" reply {
                    """
                     receive response with shopping cart info
                     display the product list and the total price
                    """.trimIndent()
                }
            }

            flow("call bff api") {
                Web.AxiosClient call Bff.Controller withApi getShoppingCartFromBffApi.onSuccess {
                    """
                    {
                        products: [{
                            id: 10001
                            name: "i'm a product",
                            amount: 1,
                            price: 500.00,
                            total: 500.00
                        }],
                        total: 500.00
                    }
                    """
                }
            }

            flow("call service") {
                Bff.Controller call Bff.Service accept "retrieve user id from authentication header"
            }

            flow("call feign client") {
                Bff.Service call Bff.FeignClient
            }

            flow("call backend api") {
                Bff.FeignClient call Backend.Controller withApi getShoppingCartApi.onSuccess {
                    """
                    {
                        products: [{
                            id: 10001
                            name: "i'm a product",
                            amount: 1,
                            price: 500.00,
                            total: 500.00
                        }],
                        total: 500.00
                    }
                    """
                }
            }

            flow("call usecase") {
                Backend.Controller call Backend.Usecase accept "call usecase to find the shopping cart by user id"
            }

            flow("call domain service") {
                Backend.Usecase call Backend.DomainService
            }

            flow("call domain repo") {
                Backend.DomainService call Backend.DomainRepository
            }

            flow("call dao and client to collect data") {
                Backend.RepositoryImpl call Backend.JpaDao accept {
                    """
                        implement domain repository and search shopping cart in db
                        get shopping cart with product id
                   """
                }
                Backend.RepositoryImpl call Backend.FeignClient accept "get product by id" reply "returns shopping cart"
            }

            flow("call db") {
                Backend.JpaDao call Backend.Postgres accept "use h2"
            }

            flow("call api") {
                Backend.FeignClient call Backend.HttpClient accept "use Wiremock"
            }
        }

        ac {
            description {
                """
                    dsl demo
                """.trimIndent()
            }

            mockup("img/google-logo.jpeg")
            mockup("https://www.thehawk.in/hawkcontent/servlet/RDESController?command=rdm.Picture&sessionId=RDWEBD9ZXBMHEVOZY44KG0ZIGGJKEI9YBQY0U&app=rdes&partner=thehawk&type=7&uid=7fqeTRRcJ4ftYOyw41rbQiUSYivnjIvrF5837497")

            link("Google 1", "https://www.google.com")
            link("Google 2", "https://www.google.com")

            val redirectApi = API("Redirect to Google", HttpMethod.GET, "/go-google")

            flow("nested calls") {
                Web.UiComponent call Web.AxiosClient accept "click" reply "send request" then {
                    Bff.Controller withApi redirectApi.onSuccess() then {
                        Bff.Service accept "execute" then {
                            Bff.FeignClient withApi redirectApi.onSuccess()
                        }
                    }
                }
            }
        }
    },
    tracking = {
        decomposition(ZhangSan, 200)
        development(LiSi, "2021-05-19")

        ac {
            flow("1-1") {
                process() cost 5
                process() cost 5
                process() cost 5
            }
            flow("1-2") {
                process() cost 20
            }
            flow("1-3") {
                process() cost 15
            }
            flow("1-4") {
                process() cost 15
            }
            flow("1-5") {
                process() cost 15
            }
            flow("1-6") {
                process() cost 15
            }
            flow("1-7") {
                process() cost 15
            }
            flow("1-8") {
                process() cost 15
            }
            flow("1-9") {
                process() cost 15
            }
            flow("1-10") {
                process() cost 15
            }
        }

        ac {
            flow("2-1") {
                process() cost 15
            }
            flow("2-2") {
                process() cost 15
            }
            flow("2-3") {
                process() cost 15
            }
            flow("2-4") {
                process() cost 15
            }
            flow("2-5") {
                process() cost 15
            }
            flow("2-6") {
                process() cost 15 blocked 100 by BlockType.STUDY cause "learn how to retrieve id from jwt in header"
            }
            flow("2-7") {
                process() cost 15
            }
            flow("2-8") {
                process() cost 15
            }
            flow("2-9") {
                process() cost 30
                process() cost 20 blocked 200 by BlockType.INTEGRATION_TESTING cause "test feign client with upstream services"
            }
            flow("2-10") {
                process() cost 15
            }
            flow("2-11") {
                process() cost 15
            }
        }

        ac {
            flow {
                process() cost 15
                process() cost 15
                process() cost 15
                process() cost 15
            }
        }

        finish("2021-05-27")
    }
)

fun main() {
    Story10001.print()
}
