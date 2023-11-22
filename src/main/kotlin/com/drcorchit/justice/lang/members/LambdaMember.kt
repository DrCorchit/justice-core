package com.drcorchit.justice.lang.members

import com.drcorchit.justice.lang.evaluators.Evaluator
import com.google.common.collect.ImmutableList

open class LambdaMember<T : Any>(
    name: String,
    description: String,
    argTypes: List<Evaluator<*>>,
    returnType: Evaluator<*>?,
    private val impl: (T, List<Any>) -> Any?
) : AbstractMember<T>(name, description, ImmutableList.copyOf(argTypes), returnType) {
    override fun apply(instance: T, args: List<Any>): Any? {
        return impl.invoke(instance, args)
    }
}