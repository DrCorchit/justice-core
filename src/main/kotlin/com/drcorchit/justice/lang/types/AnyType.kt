package com.drcorchit.justice.lang.types

import com.drcorchit.justice.lang.members.Member
import com.drcorchit.justice.lang.members.lambda.LambdaFieldMember
import com.drcorchit.justice.lang.types.primitives.StringType
import com.google.common.collect.ImmutableMap

object AnyType : NonSerializableType<Any>(Any::class) {
    override val members: ImmutableMap<String, Member<Any>> = listOf(
        LambdaFieldMember(
            this,
            "toString",
            "Returns a string representation of the object.",
            StringType
        ) { it.toString() }
    ).toMemberMap()

    override fun cast(instance: Any): Any {
        return instance
    }

    override fun accept(other: Type<*>): Boolean {
        return true
    }
}