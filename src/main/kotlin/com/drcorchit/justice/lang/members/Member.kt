package com.drcorchit.justice.lang.members

import com.drcorchit.justice.lang.evaluators.Evaluator
import com.google.common.collect.ImmutableList

abstract class Member<T>(
    val name: String,
    val description: String,
    val argTypes: ImmutableList<Evaluator<*>>,
    val returnType: Evaluator<*>?
) {
    abstract fun apply(instance: T, args: List<Any>): Any?
}