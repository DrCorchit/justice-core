package com.drcorchit.justice.lang.members

import com.drcorchit.justice.lang.types.TypedThing

interface FieldMember<T : Any> : Member<T> {
    fun get(instance: T): Any? {
        return apply(instance, listOf())
    }

    fun getAndWrap(instance: T): TypedThing<*> {
        return returnType.wrap(apply(instance, listOf())!!)
    }
}