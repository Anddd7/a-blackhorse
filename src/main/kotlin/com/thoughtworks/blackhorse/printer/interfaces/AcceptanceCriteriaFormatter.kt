package com.thoughtworks.blackhorse.printer.interfaces

import com.thoughtworks.blackhorse.schema.story.AcceptanceCriteria

interface AcceptanceCriteriaFormatter {
    fun anchors(items: List<AcceptanceCriteria>): String
    fun acceptanceCriteria(items: List<AcceptanceCriteria>): String
    fun summary(items: List<AcceptanceCriteria>): String
}
