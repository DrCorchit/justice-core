package com.drcorchit.justice.lang.members

import com.drcorchit.justice.lang.evaluators.Evaluator

open class LambdaFieldMember<T : Any>(
    name: String,
    description: String,
    returnType: Evaluator<*>,
    getter: (T) -> Any?
) : LambdaMember<T>(name, description, listOf(), returnType, { instance, _ -> getter.invoke(instance) }), FieldMember<T>