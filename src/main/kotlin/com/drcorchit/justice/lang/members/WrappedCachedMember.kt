package com.drcorchit.justice.lang.members

import com.drcorchit.justice.lang.annotations.CachedField
import com.drcorchit.justice.lang.evaluators.Evaluator
import kotlin.reflect.KCallable

class WrappedCachedMember<T : Any>(private val member: KCallable<*>, annotation: CachedField) :
    CachedFieldMember<T>(member.name, annotation.description, member.returnType.let { Evaluator.fromType(it) }) {

    init {
        require(argTypes.size == 1) { "Member $name is marked as a field, but has one or more arguments." }
    }

    override fun apply(instance: T, args: List<Any>): Any? {
        val temp = (listOf(instance) + args).toTypedArray()
        return member.call(*temp)
    }
}