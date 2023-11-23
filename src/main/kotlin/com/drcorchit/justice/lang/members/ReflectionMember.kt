package com.drcorchit.justice.lang.members

import com.drcorchit.justice.lang.types.source.TypeSource
import com.google.common.collect.ImmutableList
import kotlin.reflect.KCallable
import kotlin.reflect.KClass

open class ReflectionMember<T : Any>(
    val types: TypeSource,
    clazz: Class<T>,
    open val member: KCallable<*>,
    description: String,
    hasSideEffects: Boolean,
) : AbstractMember<T>(
    clazz,
    member.name,
    description,
    ImmutableList.copyOf(member.parameters.map { types.getType(it.type.classifier as KClass<*>) }),
    types.getType(member.returnType.classifier as KClass<*>),
    hasSideEffects
) {

    override fun apply(instance: T, args: List<Any?>): Any? {
        val temp = (listOf(instance) + args).toTypedArray()
        return member.call(*temp)
    }

    companion object {
        //Intended for internal use.

    }
}