package com.drcorchit.justice.lang.members

import com.drcorchit.justice.lang.types.Type
import com.drcorchit.justice.lang.types.TypedThing
import com.google.common.collect.ImmutableList

interface Member<T : Any> {
    val clazz: Class<T>
    val name: String
    val description: String
    val argTypes: ImmutableList<Type<*>>
    val returnType: Type<*>
    val hasSideEffects: Boolean

    fun apply(instance: T, args: List<Any?>): Any?

    fun applyAndWrap(instance: T, args: List<Any?>): TypedThing<*> {
        return returnType.wrap(apply(instance, args)!!)
    }
}