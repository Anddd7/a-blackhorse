package com.thoughtworks.blackhorse.reporter.interfaces

import com.thoughtworks.blackhorse.schema.performance.Performance

interface PerformanceFormatter {
    fun performances(items: List<Performance>): String
}
