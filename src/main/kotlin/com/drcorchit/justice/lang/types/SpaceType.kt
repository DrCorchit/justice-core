package com.drcorchit.justice.lang.types

import com.drcorchit.justice.lang.members.Member
import com.drcorchit.justice.lang.types.Type.Companion.toMemberMap
import com.drcorchit.justice.utils.math.Space
import com.google.common.collect.ImmutableMap

class SpaceType: Type<Space> {
    override val clazz = Space::class.java
    override val members: ImmutableMap<String, Member<Space>> = listOf<Member<Space>>(

    ).toMemberMap()
}