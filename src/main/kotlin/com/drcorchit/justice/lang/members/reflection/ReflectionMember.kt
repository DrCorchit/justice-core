package com.drcorchit.justice.lang.members.reflection

import com.drcorchit.justice.game.evaluation.universe.TypeUniverse
import com.drcorchit.justice.game.evaluation.environment.Parameters
import com.drcorchit.justice.game.evaluation.environment.Parameters.Companion.toEnv
import com.drcorchit.justice.lang.members.AbstractMember
import com.drcorchit.justice.lang.types.Type
import com.google.common.collect.ImmutableList
import kotlin.reflect.KCallable
import kotlin.reflect.KClass
import kotlin.reflect.KParameter
import kotlin.reflect.KType
import kotlin.reflect.full.createType
import kotlin.reflect.full.valueParameters

open class ReflectionMember<T : Any>(
    val types: TypeUniverse,
    type: Type<in T>,
    open val member: KCallable<*>,
    description: String,
    hasSideEffects: Boolean,
) : AbstractMember<T>(
    type,
    member.name,
    description,
    argsToTypes(types, member.valueParameters),
    kTypeToType(types, member.returnType),
    hasSideEffects
) {

    override fun apply(instance: T, args: List<Any>): Any {
        val temp = (listOf(instance) + args).toTypedArray()
        return member.call(*temp) ?: Unit
    }

    companion object {
        fun argsToTypes(types: TypeUniverse, params: List<KParameter>): Parameters {
            return params.associate { it.name!! to kTypeToType(types, it.type) }.toEnv()
        }

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