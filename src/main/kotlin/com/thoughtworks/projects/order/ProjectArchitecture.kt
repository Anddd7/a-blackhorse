package com.thoughtworks.projects.order

import com.thoughtworks.blackhorse.schema.architecture.Client
import com.thoughtworks.blackhorse.schema.architecture.Component
import com.thoughtworks.blackhorse.schema.architecture.Container
import com.thoughtworks.blackhorse.schema.architecture.DB
import com.thoughtworks.blackhorse.schema.architecture.Endpoint
import com.thoughtworks.blackhorse.schema.architecture.at
import com.thoughtworks.blackhorse.schema.architecture.cost

object Web : Container {
    object UiComponent : Component
    object AxiosClient : Component

    override val id = "1"
    override val definitions = listOf(
        UiComponent call UiComponent cost 60 at {
            """
            - Just import related ui component, testing with snapshot
            """.trimIndent()
        },
        UiComponent mock AxiosClient cost 60 at {
            """
            - Mock axios client
            - Call axios client, assert component state
            """.trimIndent()
        },
        AxiosClient fake Bff.Controller at {
            """
            - Fake api endpoint
            - Call fake api, assert the response and error handling is correct
            """.trimIndent()
        },
    )
}

object Bff : Container {
    object Controller : Endpoint
    object Service : Component
    object FeignClient : Client

    override val id = "2"
    override val definitions = listOf(
        Controller mock Service cost 60 at {
            """
            - Mock service
            - Call service, verify the expected input parameters and assert the expected output return
            """.trimIndent()
        },
        Service mock FeignClient cost 60 at {
            """
            - Mock feign client
            - Call feign client, verify the expected input parameters and assert the expected output return
            """.trimIndent()
        },
        FeignClient fake Backend.Controller cost 60 at {
            """
                - Setup endpoints in wiremock with fake payload
                - Setup wiremock's url as base url
                - Call upstream endpoints and verify the data object that formatted from json is expected
            """.trimIndent()
        }
    )
}

object Backend : Container {
    object Controller : Endpoint

    object Usecase : Component

    object DomainService : Component
    object DomainRepository : Component

    object RepositoryImpl : Component
    object FeignClient : Client
    object JpaDao : Component

    object Postgres : DB
    object HttpClient : Component

    override val id = "3"
    override val definitions = listOf(
        Controller mock Usecase cost 60 at {
            """
            - Mock usecase
            - Call usecase, verify the expected input parameters and assert the expected output return
            """.trimIndent()
        },
        Usecase mock DomainService cost 60 at {
            """
            - Mock domain service
            - Call domain service, verify the expected input parameters and assert the expected output return
            """.trimIndent()
        },
        DomainService mock DomainRepository cost 60 at {
            """
            - Mock domain repository
            - Call domain repository, verify the expected input parameters and assert the expected output return
            """.trimIndent()
        },
        RepositoryImpl mock FeignClient cost 60 at {
            """
            - Mock feign client
            - Call feign client, verify the expected input parameters and assert the expected output return
            """.trimIndent()
        },
        FeignClient mock HttpClient cost 60 at {
            """
            - Fake http client (using wiremock)
            - Call http client, stub the request and response and assert the expected response status and payload
            """.trimIndent()
        },
        RepositoryImpl mock JpaDao cost 60 at {
            """
            - Mock jpa dao
            - Call jpa dao, verify the expected input parameters and assert the expected output return
            """.trimIndent()
        },
        JpaDao mock Postgres cost 60 at {
            """
            - Fake db (using h2 or docker)
            - Call fake db, init some test data and assert the execution result set is expected
            """.trimIndent()
        },
    )
}
