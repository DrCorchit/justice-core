package com.drcorchit.justice.lang.members

import com.drcorchit.justice.game.evaluation.TypeUniverse
import com.drcorchit.justice.lang.types.Type
import com.google.common.collect.ImmutableList
import kotlin.reflect.KCallable
import kotlin.reflect.KClass
import kotlin.reflect.KType
import kotlin.reflect.full.createType

open class ReflectionMember<T : Any>(
    val types: TypeUniverse,
    clazz: Class<T>,
    open val member: KCallable<*>,
    description: String,
    hasSideEffects: Boolean,
) : AbstractMember<T>(
    clazz,
    member.name,
    description,
    ImmutableList.copyOf(member.parameters.map { kTypeToType(types, it.type) }),
    kTypeToType(types, member.returnType),
    hasSideEffects
) {

    override fun apply(instance: T, args: List<Any>): Any {
        val temp = (listOf(instance) + args).toTypedArray()
        return member.call(*temp) ?: Unit
    }

    companion object {
        //Intended for internal use.
        fun kTypeToType(types: TypeUniverse, kType: KType): Type<*> {
            val clazz = kType.classifier as KClass<*>
            val params = kType.arguments.map { it.type ?: Any::class.createType() }
                .map { kTypeToType(types, it) }
                .let { ImmutableList.copyOf(it) }
            return types.getType(clazz to params)
        }
    }
}