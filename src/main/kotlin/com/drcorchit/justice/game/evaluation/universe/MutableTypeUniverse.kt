package com.drcorchit.justice.game.evaluation.universe

import com.drcorchit.justice.game.evaluation.instantiators.JusticeTypeInstantiator
import com.drcorchit.justice.game.evaluation.instantiators.SimpleTypeInstantiator
import com.drcorchit.justice.game.evaluation.instantiators.TypeInstantiator
import com.drcorchit.justice.lang.types.Type
import com.drcorchit.justice.utils.Utils
import com.google.common.cache.LoadingCache
import kotlin.reflect.KClass

class MutableTypeUniverse : TypeUniverse {
    private val typesBySimpleName: MutableMap<String, KClass<*>> = mutableMapOf()
    private val typeInstantiators: MutableList<TypeInstantiator> = mutableListOf()
    private val defaultInstantiator = JusticeTypeInstantiator(this)
    private val cache: LoadingCache<TypeParameters, Type<*>> = Utils.createCache(1000) { candidateSubclass ->
        (typeInstantiators.firstOrNull { it.matches(candidateSubclass) } ?: defaultInstantiator)
            .instantiate(candidateSubclass)
    }

    override fun parseSimpleType(name: String): KClass<*> {
        return typesBySimpleName.getOrElse(name) { Class.forName(name).kotlin }
    }

    override fun getType(params: TypeParameters): Type<*> {
        return cache.get(params)
    }

    fun registerName(simpleName: String, type: KClass<*>) {
        typesBySimpleName[simpleName] = type
    }

    fun registerType(type: Type<*>) {
        registerType(SimpleTypeInstantiator(type))
    }

    fun registerType(instantiator: TypeInstantiator) {
        typeInstantiators.add(instantiator)
    }
}