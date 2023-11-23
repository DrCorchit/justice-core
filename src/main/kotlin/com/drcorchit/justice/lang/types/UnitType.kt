package com.drcorchit.justice.lang.types

import com.drcorchit.justice.lang.members.Member
import com.google.common.collect.ImmutableMap

object UnitType : NonSerializableType<Unit>() {
    override val clazz = Unit::class.java
    override val members: ImmutableMap<String, Member<Unit>> = ImmutableMap.of()
}