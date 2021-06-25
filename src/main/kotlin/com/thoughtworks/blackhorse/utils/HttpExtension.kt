package com.thoughtworks.blackhorse.utils

import com.google.gson.Gson
import com.thoughtworks.blackhorse.printer.jira.api.JiraRestConfig
import com.thoughtworks.blackhorse.printer.markdown.formatter.toLines
import com.thoughtworks.blackhorse.schema.story.HttpMethod
import org.apache.http.HttpEntity
import org.apache.http.client.methods.CloseableHttpResponse
import org.apache.http.client.methods.HttpDelete
import org.apache.http.client.methods.HttpEntityEnclosingRequestBase
import org.apache.http.client.methods.HttpGet
import org.apache.http.client.methods.HttpPost
import org.apache.http.client.methods.HttpPut
import org.apache.http.client.methods.HttpRequestBase
import org.apache.http.impl.client.HttpClients
import java.io.BufferedReader
import java.io.InputStreamReader
import kotlin.streams.toList

private val gson = Gson()
fun logApiPayload(text: String?) = text?.let { println(">\t$it") }

object HttpClient {
    fun execute(method: HttpMethod, url: String, entity: HttpEntity? = null) = execute<String>(method, url, entity)

    inline fun <reified T> execute(method: HttpMethod, url: String, entity: HttpEntity? = null): T? {
        HttpClients.createDefault().use { client ->
            val request = buildRequest(method, url, entity)
            val response: CloseableHttpResponse? = client.execute(request)

            return response?.mapTo(T::class.java)
        }
    }

    @Suppress("UNCHECKED_CAST")
    fun <T> CloseableHttpResponse.mapTo(mapping: Class<T>): T? = use {
        logApiPayload("Response: ${statusLine.statusCode}")

        runCatching {
            val reader = BufferedReader(InputStreamReader(entity.content))
            when (mapping) {
                String::class.java -> reader.lines().toList().toLines() as T
                else -> gson.fromJson(reader, mapping)
            }
        }
            .onFailure { e -> logApiPayload(e.message) }
            .getOrNull()
    }

    fun buildRequest(method: HttpMethod, url: String, entity: HttpEntity?): HttpRequestBase {
        val request = when (method) {
            HttpMethod.POST -> HttpPost(url)
            HttpMethod.PUT -> HttpPut(url)
            HttpMethod.GET -> HttpGet(url)
            HttpMethod.DELETE -> HttpDelete(url)
        }
        request.setHeaders(JiraRestConfig.headers())
        if (request is HttpEntityEnclosingRequestBase) request.entity = entity

        logApiPayload("Request: ${request.method} ${request.uri}")

        return request
    }
}
