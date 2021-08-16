package com.thoughtworks.projects.rental.onboarding.architecture

import com.thoughtworks.blackhorse.schema.architecture.Container
import com.thoughtworks.blackhorse.schema.architecture.ProcessDefinitionBuilder
import com.thoughtworks.blackhorse.schema.architecture.attributes.ContainerLayer
import com.thoughtworks.blackhorse.schema.architecture.attributes.TechStack
import com.thoughtworks.blackhorse.schema.performance.attributes.Member

object GeneralWeb : Container {
    override val id: String = "1"
    override val definitions: List<ProcessDefinitionBuilder>
        get() = TODO("Not yet implemented")
    override val layer: ContainerLayer
        get() = TODO("Not yet implemented")
    override val techStack: List<TechStack>
        get() = TODO("Not yet implemented")
    override val owner: List<Member>
        get() = TODO("Not yet implemented")
    override val responsibility: String
        get() = TODO("Not yet implemented")
}
