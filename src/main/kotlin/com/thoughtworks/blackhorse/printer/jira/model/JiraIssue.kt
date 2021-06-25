package com.thoughtworks.blackhorse.printer.jira.model

data class JiraIssue(
    val id: String,
    val key: String,
    val fields: JiraIssueFields,
)

data class JiraIssueFields(
    val summary: String,
    val attachment: List<JiraAttachment>,
)

data class JiraAttachment(
    val id: String,
    val filename: String,
)
