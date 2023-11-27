package com.drcorchit.justice.game.evaluation.universe

import com.drcorchit.justice.game.evaluation.instantiators.JusticeTypeInstantiator
import com.drcorchit.justice.game.evaluation.instantiators.TypeInstantiator
import com.drcorchit.justice.lang.types.Type
import com.drcorchit.justice.utils.Utils
import com.google.common.cache.LoadingCache
import com.google.common.collect.ImmutableList
import com.google.common.collect.ImmutableMap
import kotlin.reflect.KClass

class ImmutableTypeUniverse(
    private val typesBySimpleName: ImmutableMap<String, KClass<*>>,
    private val typesBySuperClass: ImmutableList<TypeInstantiator>
) : TypeUniverse {
    private val defaultInstantiator = JusticeTypeInstantiator(this)
    private val cache: LoadingCache<TypeParameters, Type<*>> = Utils.createCache(1000) { candidateSubclass ->
        (typesBySuperClass.firstOrNull { it.matches(candidateSubclass) } ?: defaultInstantiator)
            .instantiate(candidateSubclass)
    }

    override fun parseSimpleType(name: String): KClass<*> {
        return typesBySimpleName.getOrElse(name) { Class.forName(name).kotlin }
    }

    override fun getType(params: TypeParameters): Type<*> {
        return cache.get(params)
    }
}