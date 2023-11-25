package com.drcorchit.justice.lang.members

import com.drcorchit.justice.lang.types.Type

open class LambdaFieldMember<T : Any>(
    clazz: Class<T>,
    name: String,
    description: String,
    returnType: Type<*>,
    getter: (T) -> Any
) : LambdaMember<T>(
    clazz,
    name,
    description,
    listOf(),
    returnType,
    false,
    { instance, _ -> getter.invoke(instance) }), FieldMember<T>