package com.thoughtworks.blackhorse.schema.architecture

interface Node {
    fun name() = javaClass.run { canonicalName.substring(packageName.length + 1) }
}

// webapp
interface Web : Container

// e.g. nginx, api gateway, bff
interface Proxy : Container

// api server
interface Server : Container

// api endpoint
interface Endpoint : Component

// http client
interface Client : Component

// e.g. jpa, jdbc
interface DB : Component
