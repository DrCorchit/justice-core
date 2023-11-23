package com.drcorchit.justice.lang.types

import com.drcorchit.justice.lang.types.primitives.StringType
import com.drcorchit.justice.lang.members.LambdaFieldMember
import com.drcorchit.justice.lang.members.Member
import com.google.common.collect.ImmutableMap

object TypeType : NonSerializableType<Type<*>>() {
    override val clazz = Type::class.java
    override val members: ImmutableMap<String, Member<Type<*>>> = ImmutableMap.copyOf(listOf(
        LambdaFieldMember(
            Type::class.java,
            "name",
            "The name of the type",
            StringType
        ) { it.clazz.name }
    ).associateBy { it.name })
}