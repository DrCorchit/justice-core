package com.drcorchit.justice.lang.evaluators

import com.drcorchit.justice.lang.members.LambdaFieldMember
import com.google.common.collect.ImmutableMap

object AnyEvaluator: NonSerializableEvaluator<Any>() {
    override val clazz = Any::class
    override val members: ImmutableMap<String, LambdaFieldMember<Any>> = listOf(
        LambdaFieldMember<Any>("toString", "Returns a string representation of the object.", StringEvaluator) { it.toString() }
    ).associateBy { it.name }.let { ImmutableMap.copyOf(it) }
}