package com.thoughtworks.blackhorse.config

import java.util.Properties

object GlobalConfig {
    val projects: Map<String, Properties> by lazy {
        val properties = loadProperties("projects.properties")
        val projectKeys = properties.getProperty("projects")
            ?.split(",")
            ?.map { it.trim().lowercase() }
            ?: throw IllegalArgumentException("{projects} is missing in projects.properties")
        projectKeys.associateWith { loadProperties("projects/$it/configure.properties") }
    }

    private fun loadProperties(path: String) =
        GlobalConfig.javaClass.classLoader.getResourceAsStream(path).use {
            Properties().apply { load(it) }
        }
}
