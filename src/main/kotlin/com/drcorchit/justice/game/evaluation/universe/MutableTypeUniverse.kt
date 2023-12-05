package com.drcorchit.justice.game.evaluation.universe

import com.drcorchit.justice.game.evaluation.typeFactories.JusticeTypeFactory
import com.drcorchit.justice.game.evaluation.typeFactories.SimpleTypeFactory
import com.drcorchit.justice.game.evaluation.typeFactories.TypeFactory
import com.drcorchit.justice.lang.types.Type
import com.drcorchit.justice.utils.Utils
import com.google.common.cache.LoadingCache
import kotlin.reflect.KClass

class MutableTypeUniverse : TypeUniverse {
    private val typesBySimpleName: MutableMap<String, KClass<*>> = mutableMapOf()
    private val typeFactories: MutableList<TypeFactory> = mutableListOf()
    private val defaultInstantiator = JusticeTypeFactory(this)
    private val cache: LoadingCache<TypeParameters, Type<*>> = Utils.createCache(1000) { candidateSubclass ->
        (typeFactories.firstOrNull { it.matches(candidateSubclass) } ?: defaultInstantiator)
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
        registerType(SimpleTypeFactory(type))
    }

    fun registerType(instantiator: TypeFactory) {
        typeFactories.add(instantiator)
    }

    fun sort() {
        typeFactories.sort()
    }

    override fun toString(): String {
        return typeFactories.toString()
    }
}