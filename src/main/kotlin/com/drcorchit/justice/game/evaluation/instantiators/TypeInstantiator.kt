package com.drcorchit.justice.game.evaluation.instantiators

import com.drcorchit.justice.game.evaluation.universe.TypeParameters
import com.drcorchit.justice.game.evaluation.universe.kClass
import com.drcorchit.justice.lang.types.Type
import kotlin.reflect.KClass
import kotlin.reflect.full.isSuperclassOf

abstract class TypeInstantiator(val kClass: KClass<*>) {

    fun matches(typeParameters: TypeParameters): Boolean {
        return kClass.isSuperclassOf(typeParameters.kClass)
    }

    abstract fun instantiate(typeParameters: TypeParameters): Type<*>
}