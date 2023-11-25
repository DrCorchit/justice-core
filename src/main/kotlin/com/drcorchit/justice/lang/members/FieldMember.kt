package com.drcorchit.justice.lang.members

import com.drcorchit.justice.lang.code.Thing

interface FieldMember<T : Any> : Member<T> {
    fun get(instance: T): Any {
        return apply(instance, listOf())
    }

    fun getAndWrap(instance: T): Thing<*> {
        return returnType.wrap(get(instance))
    }
}