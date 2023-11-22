package com.drcorchit.justice.lang.members

import com.drcorchit.justice.lang.evaluators.Evaluator
import com.google.common.collect.ImmutableList

interface Member<T : Any> {
    val clazz: Class<T>
    val name: String
    val description: String
    val argTypes: ImmutableList<Evaluator<*>>
    val returnType: Evaluator<*>?

    fun apply(instance: T, args: List<Any?>): Any?

    fun applyCast(instance: Any, args: List<Any?>): Any? {
        return apply(clazz.cast(instance), args)
    }
}