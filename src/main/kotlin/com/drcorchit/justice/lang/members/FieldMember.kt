package com.drcorchit.justice.lang.members

interface FieldMember<T : Any> : Member<T> {
    fun get(instance: T): Any? {
        return apply(instance, listOf())
    }

    fun getCast(instance: Any): Any? {
        return get(clazz.cast(instance))
    }
}