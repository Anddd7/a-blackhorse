package com.thoughtworks.blackhorse.reporter.interfaces

import com.thoughtworks.blackhorse.schema.performance.StoryPerformance

interface PerformanceFormatter {
    fun performances(items: List<StoryPerformance>): String
}
