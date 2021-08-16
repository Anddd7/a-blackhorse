package com.thoughtworks.blackhorse.schema.architecture

import com.thoughtworks.blackhorse.schema.architecture.attributes.ContainerLayer

open class ArchitectureOf(
    vararg groups: Pair<ContainerLayer, List<Container>>
)
