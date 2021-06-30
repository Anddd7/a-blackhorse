package com.thoughtworks.blackhorse.config

import java.util.Properties

object PropertyLoader {
    val projects: Map<String, Properties> by lazy {
        loadProjectKeys()
            .split(",")
            .map { it.trim().lowercase() }
            .associateWith { loadProperties("projects/$it/configure.properties") }
    }

    private fun loadProjectKeys() = loadProperties("projects.properties").getProperty("projects")
        ?: throw IllegalArgumentException("{projects} is missing in projects.properties")

    private fun loadProperties(path: String) =
        PropertyLoader.javaClass.classLoader.getResourceAsStream(path).use {
            Properties().apply { load(it) }
        }
}
