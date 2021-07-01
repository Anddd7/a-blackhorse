package com.thoughtworks.blackhorse.printer.markdown.formatter

import com.thoughtworks.blackhorse.printer.interfaces.ApiSchemaFormatter
import com.thoughtworks.blackhorse.schema.story.attributes.APISchema
import com.thoughtworks.blackhorse.schema.story.attributes.ApiScenario

class MarkdownApiSchemaFormatter : ApiSchemaFormatter {
    override fun anchors(items: List<APISchema>) = when {
        items.isEmpty() -> ""
        else -> toAnchorLink("API Schema")
    }

    override fun schemas(items: List<APISchema>) = when {
        items.isEmpty() -> ""
        else -> lineOf(
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

    private fun String.formatAsJson(label: String) =
        lineOf(
            "- $label",
            "```json",
            this,
            "```"
        ).prependIndent("  ")

    private fun scenario(scenario: ApiScenario): String {
        return lineOf(
            "- ${scenario.statusDescription}",
            scenario.request?.formatAsJson("Request"),
            scenario.request?.formatAsJson("Response"),
        )
    }
}
