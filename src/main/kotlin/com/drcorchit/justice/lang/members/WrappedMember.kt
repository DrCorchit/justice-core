package com.drcorchit.justice.lang.members

import com.drcorchit.justice.lang.evaluators.Evaluator
import com.google.common.collect.ImmutableList
import kotlin.reflect.KCallable

open class WrappedMember<T : Any>(
    open val member: KCallable<*>,
    description: String,
) : AbstractMember<T>(
    member.name,
    description,
    ImmutableList.copyOf(member.parameters.map { it.type }.map { Evaluator.fromType(it) }),
    member.returnType.let { Evaluator.fromType(it) }
) {

    override fun apply(instance: T, args: List<Any>): Any? {
        val temp = (listOf(instance) + args).toTypedArray()
        return member.call(*temp)
    }
}