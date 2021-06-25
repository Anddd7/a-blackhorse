package com.thoughtworks.blackhorse.schema.story

data class APISchema(
    val api: API,
    val scenarios: List<ApiScenario>,
)

data class API(
    val name: String,
    val method: HttpMethod,
    val url: String,
    val request: () -> String? = { null }
) {
    fun onSuccess(httpStatus: HttpStatus = HttpStatus.OK, response: () -> String? = { null }) =
        onStatus(httpStatus, response)

    fun onFailed(httpStatus: HttpStatus = HttpStatus.INTERNAL_SERVER_ERROR, response: () -> String? = { null }) =
        onStatus(httpStatus, response)

    private fun onStatus(httpStatus: HttpStatus, response: () -> String?) = onStatus(httpStatus, response())
    private fun onStatus(httpStatus: HttpStatus, response: String?) =
        ApiScenario(this, httpStatus, response = response?.trimIndent())

    val identifier = "${method.name} $url"
}

data class ApiScenario(
    val api: API,
    val statusCode: HttpStatus,
    val request: String? = null,
    val response: String? = null,
) {
    val apiDefinition = api.identifier
    val statusDescription = statusCode.run { "$code $name" }
}
