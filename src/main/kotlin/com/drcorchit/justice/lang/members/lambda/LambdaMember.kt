package com.drcorchit.justice.lang.members.lambda

import com.drcorchit.justice.game.evaluation.environment.Parameters
import com.drcorchit.justice.lang.members.AbstractMember
import com.drcorchit.justice.lang.types.Type

open class LambdaMember<T : Any>(
    type: Type<in T>,
    name: String,
    description: String,
    args: Parameters,
    returnType: Type<*>,
    hasSideEffects: Boolean,
    private val impl: (T, List<Any>) -> Any
) : AbstractMember<T>(type, name, description, args, returnType, hasSideEffects) {
    override fun apply(instance: T, args: List<Any>): Any {
        return impl.invoke(instance, args)
    }
}