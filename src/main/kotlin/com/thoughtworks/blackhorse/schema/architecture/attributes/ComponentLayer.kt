package com.thoughtworks.blackhorse.schema.architecture.attributes

interface ComponentLayer {
    fun value(): String
}

enum class Mvc : ComponentLayer {
    Model, View, Controller;

    override fun value(): String = name
}

enum class Hexagon : ComponentLayer {
    Inbound, Outbound, Application, Domain;

    override fun value(): String = name
}

enum class ThreeLayered : ComponentLayer {
    Controller, Service, Repository;

    override fun value(): String = name
}

enum class FourLayered : ComponentLayer {
    UserInterface, Application, Domain, Infrastructure;

    override fun value(): String = name
}

enum class CQRS : ComponentLayer {
    UserInterface, Command, Domain, Repository,
    EventStore, EventBus, EventHandler, Query, ThinData, Data;

    override fun value(): String = name
}
