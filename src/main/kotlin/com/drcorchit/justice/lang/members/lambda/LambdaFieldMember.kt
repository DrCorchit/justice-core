package com.drcorchit.justice.lang.members.lambda

import com.drcorchit.justice.game.evaluation.environment.Parameters
import com.drcorchit.justice.lang.members.FieldMember
import com.drcorchit.justice.lang.types.Type

open class LambdaFieldMember<T : Any>(
    type: Type<in T>,
    name: String,
    description: String,
    returnType: Type<*>,
    getter: (T) -> Any
) : LambdaMember<T>(
    type,
    name,
    description,
    Parameters.EMPTY,
    returnType,
    false,
    { instance, _ -> getter.invoke(instance) }), FieldMember<T>