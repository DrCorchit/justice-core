package com.drcorchit.justice.game.evaluation.typeFactories

import com.drcorchit.justice.game.evaluation.universe.TypeParameters
import com.drcorchit.justice.game.evaluation.universe.kClass
import com.drcorchit.justice.lang.types.Type
import kotlin.reflect.KClass
import kotlin.reflect.full.isSubclassOf
import kotlin.reflect.full.isSuperclassOf

abstract class TypeFactory(val kClass: KClass<*>) : Comparable<TypeFactory> {

    open fun matches(typeParameters: TypeParameters): Boolean {
        return kClass.isSuperclassOf(typeParameters.kClass)
    }

    abstract fun instantiate(typeParameters: TypeParameters): Type<*>

    //We need for these to be partially ordered so that the most specific type instantiator gets used.
    override fun compareTo(other: TypeFactory): Int {
        val isSub = kClass.isSubclassOf(other.kClass)
        val isSuper = kClass.isSuperclassOf(other.kClass)
        return if (isSub && !isSuper) -1 else if (isSuper && !isSub) 1 else 0
    }

    override fun toString(): String {
        return "${this::class.simpleName}<${kClass.qualifiedName}>"
    }

    override fun hashCode(): Int {
        return kClass.hashCode()
    }

    override fun equals(other: Any?): Boolean {
        return if (other is TypeFactory) kClass == other.kClass else false
    }
}