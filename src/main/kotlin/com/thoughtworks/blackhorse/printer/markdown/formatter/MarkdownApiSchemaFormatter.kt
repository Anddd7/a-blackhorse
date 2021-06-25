package com.thoughtworks.blackhorse.printer.markdown.formatter

import com.thoughtworks.blackhorse.printer.interfaces.ApiSchemaFormatter
import com.thoughtworks.blackhorse.schema.story.APISchema
import com.thoughtworks.blackhorse.schema.story.ApiScenario

class MarkdownApiSchemaFormatter : ApiSchemaFormatter {
    override fun anchors(items: List<APISchema>): String {
        if (items.isEmpty()) return ""

        return toAnchorLink("API Schema")
    }

    override fun schemas(items: List<APISchema>): String {
        if (items.isEmpty()) return ""

        return lineOf(
            "### API Schema",
            items.map(::schema).toLines()
        )
    }

    private fun schema(schema: APISchema) = schema.run {
        lineOf(
            "#### ${api.name}",
            "> ${api.identifier}",
            scenarios.mapToLines(::scenario)
        )
    }

    private fun scenario(scenario: ApiScenario): String {
        val formattedRequest = scenario.request?.let {
            lineOf(
                "- Request",
                "```json",
                it,
                "```"
            )
        }
        val formattedResponse = scenario.response?.let {
            lineOf(
                "- Response",
                "```json",
                it,
                "```"
            )
        }

        return lineOf(
            "- ${scenario.statusDescription}",
            formattedRequest?.prependIndent("  "),
            formattedResponse?.prependIndent("  "),
        )
    }
}
