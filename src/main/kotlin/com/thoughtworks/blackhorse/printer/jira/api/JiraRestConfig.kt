package com.thoughtworks.blackhorse.printer.jira.api

import com.thoughtworks.blackhorse.config.ProjectConfig
import com.thoughtworks.blackhorse.config.ProjectConfig.Companion.jiraBaseUrl
import org.apache.http.message.BasicHeader

object JiraRestConfig {
    fun issue(key: String) = "${jiraBaseUrl()}/issue/$key"
    fun issueAttachments(key: String) = "${issue(key)}/attachments"
    fun attachment(id: String) = "${jiraBaseUrl()}/attachment/$id"

    fun headers() = arrayOf(
        BasicHeader("X-Atlassian-Token", "no-check"),
        BasicHeader("Authorization", "Bearer " + ProjectConfig.jiraToken()),
        BasicHeader("Accept", "*/*"),
    )
}
