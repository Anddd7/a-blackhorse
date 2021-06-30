package com.thoughtworks.blackhorse.printer.jira.api

import com.thoughtworks.blackhorse.config.StoryContextHolder
import com.thoughtworks.blackhorse.printer.jira.model.JiraIssue
import com.thoughtworks.blackhorse.schema.story.HttpMethod
import com.thoughtworks.blackhorse.utils.HttpClient
import com.thoughtworks.blackhorse.utils.logApiPayload
import org.apache.http.entity.ContentType
import org.apache.http.entity.StringEntity
import org.apache.http.entity.mime.MultipartEntityBuilder
import org.apache.http.entity.mime.content.FileBody
import java.math.BigDecimal
import java.nio.file.Path
import kotlin.io.path.name

private fun uploadIssueAttachment(key: String, file: Path) {
    val context = StoryContextHolder.get().jira()

    val response = HttpClient.execute(
        HttpMethod.POST,
        context.issueAttachments(key),
        context.headers(),
        MultipartEntityBuilder.create()
            .addPart("file", FileBody(file.toFile()))
            .build()
    )
    logApiPayload(response)
}

private fun deleteAttachment(id: String) {
    val context = StoryContextHolder.get().jira()

    val response = HttpClient.execute(
        HttpMethod.DELETE,
        context.attachment(id),
        context.headers(),
    )
    logApiPayload(response)
}

private fun getIssue(key: String): JiraIssue {
    val context = StoryContextHolder.get().jira()

    val response = HttpClient.execute<JiraIssue>(
        HttpMethod.GET,
        context.issue(key),
        context.headers(),
    )
    return response ?: throw IllegalArgumentException("Invalid jira issue key")
}

// TODO estimation is not a named field in jira, so it cannot reused cross projects
fun updateCardInformation(
    key: String,
    title: String,
    points: Int,
    description: String,
) {
    val context = StoryContextHolder.get().jira()

    val response = HttpClient.execute(
        HttpMethod.PUT,
        context.issue(key),
        context.headers(),
        StringEntity(
            """
                {
                    "fields":{
                        "summary": "$title",
                        "customfield_10008": ${BigDecimal(points).setScale(1)},
                        "description": "$description"
                    }
                }
            """.trimIndent(),
            ContentType.APPLICATION_JSON
        )
    )
    logApiPayload(response)
}

fun updateAttachments(key: String, files: List<Path>) {
    val filenames = files.map(Path::name).toSet()
    val attachments = getIssue(key).fields.attachment.filter { it.filename in filenames }

    attachments.map {
        logApiPayload("find existing attachment, delete and re-upload again")
        deleteAttachment(it.id)
    }
    files.forEach {
        uploadIssueAttachment(key, it)
    }
}
