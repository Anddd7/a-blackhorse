package com.thoughtworks.blackhorse.printer.jira.api

import com.thoughtworks.blackhorse.config.ProjectContext
import com.thoughtworks.blackhorse.config.StoryContext
import org.apache.http.message.BasicHeader

class JiraRestConfig(
    private val jiraBaseUrl: String,
    private val jiraToken: String,
) {
    fun issue(key: String) = "$jiraBaseUrl/issue/$key"
    fun issueAttachments(key: String) = "$jiraBaseUrl/issue/$key/attachments"
    fun attachment(id: String) = "$jiraBaseUrl/attachment/$id"

    fun headers() = arrayOf(
        BasicHeader("X-Atlassian-Token", "no-check"),
        BasicHeader("Authorization", "Bearer $jiraToken"),
        BasicHeader("Accept", "*/*"),
    )
}

fun StoryContext.jira() = projectContext.jira()
fun ProjectContext.jira() = JiraRestConfig(jiraBasUrl, jiraToken)
