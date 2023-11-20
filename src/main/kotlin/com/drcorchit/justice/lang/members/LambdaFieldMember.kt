package com.drcorchit.justice.lang.members

import com.drcorchit.justice.lang.evaluators.Evaluator

open class LambdaFieldMember<T>(
    name: String,
    description: String,
    argTypes: List<Evaluator<*>>,
    returnType: Evaluator<*>,
    impl: (T) -> Any?
) : LambdaMember<T>(name, description, argTypes, returnType, { instance, _ -> impl.invoke(instance) }), FieldMember<T> {
    override fun get(instance: T): Any? {
        return apply(instance, listOf())
    }
}