package com.thoughtworks.blackhorse.schema.architecture

import org.slf4j.Logger
import org.slf4j.LoggerFactory

interface Node {
    fun log(): Logger = LoggerFactory.getLogger(Node::class.java)

    fun name() = javaClass.run { canonicalName.substring(packageName.length + 1) }
}
