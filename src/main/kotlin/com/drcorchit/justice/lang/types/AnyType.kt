package com.drcorchit.justice.lang.types

import com.drcorchit.justice.lang.types.primitives.StringType
import com.drcorchit.justice.lang.members.LambdaFieldMember
import com.google.common.collect.ImmutableMap

object AnyType : NonSerializableType<Any>() {
    override val clazz = Any::class.java
    override val members: ImmutableMap<String, LambdaFieldMember<Any>> = listOf(
        LambdaFieldMember(
            clazz,
            "toString",
            "Returns a string representation of the object.",
            StringType
        ) { it.toString() }
    ).associateBy { it.name }.let { ImmutableMap.copyOf(it) }
}