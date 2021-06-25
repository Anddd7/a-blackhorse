package com.thoughtworks.blackhorse.printer.jira.api

import com.thoughtworks.blackhorse.printer.jira.model.JiraIssue
import com.thoughtworks.blackhorse.schema.story.HttpMethod
import com.thoughtworks.blackhorse.utils.HttpClient
import com.thoughtworks.blackhorse.utils.logApiPayload
import org.apache.http.entity.ContentType
import org.apache.http.entity.StringEntity
import org.apache.http.entity.mime.MultipartEntityBuilder
import org.apache.http.entity.mime.content.FileBody
import java.nio.file.Path
import kotlin.io.path.name

private fun uploadIssueAttachment(key: String, file: Path) {
    val response = HttpClient.execute(
        HttpMethod.POST,
        JiraRestConfig.issueAttachments(key),
        MultipartEntityBuilder.create()
            .addPart("file", FileBody(file.toFile()))
            .build()
    )
    logApiPayload(response)
}

private fun deleteAttachment(id: String) {
    val response = HttpClient.execute(
        HttpMethod.DELETE,
        JiraRestConfig.attachment(id),
    )
    logApiPayload(response)
}

private fun getIssue(key: String): JiraIssue {
    val response = HttpClient.execute<JiraIssue>(
        HttpMethod.GET,
        JiraRestConfig.issue(key)
    )
    return response ?: throw IllegalArgumentException("Invalid jira issue key")
}

fun updateDescription(key: String, description: String) {
    val response = HttpClient.execute(
        HttpMethod.PUT,
        JiraRestConfig.issue(key),
        StringEntity(
            """
                {
                    "fields":{
                        "description": "$description"
                    }
                }
            """.trimIndent(),
            ContentType.APPLICATION_JSON
        )
    )
    logApiPayload(response)
}

fun updateAttachment(key: String, file: Path) {
    val attachment = getIssue(key).fields.attachment.filter { it.filename == file.name }
    attachment.forEach {
        println("find existing attachment, delete and re-upload again")
        deleteAttachment(it.id)
    }
    uploadIssueAttachment(key, file)
}
