package com.drcorchit.justice.game.evaluation

import com.drcorchit.justice.lang.types.ReflectionType
import com.drcorchit.justice.lang.types.Type
import com.drcorchit.justice.utils.Utils
import com.google.common.cache.LoadingCache
import com.google.common.collect.ImmutableMap
import kotlin.reflect.KClass
import kotlin.reflect.full.isSuperclassOf

class MutableTypeUniverse : TypeUniverse {
    private val typesBySimpleName: MutableMap<String, KClass<*>> = mutableMapOf()
    private val typesBySuperClass: MutableMap<KClass<*>, (TypeParameters) -> Type<*>> = mutableMapOf()
    private val cache: LoadingCache<TypeParameters, Type<*>> = Utils.createCache(1000) { candidateSubclass ->
        typesBySuperClass.entries.firstOrNull {
            it.key.isSuperclassOf(candidateSubclass.kClass)
        }?.value?.invoke(candidateSubclass) ?: ReflectionType(candidateSubclass.kClass, this)
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

    fun registerType(superclass: KClass<*>, rule: (TypeParameters) -> Type<*>) {
        typesBySuperClass[superclass] = rule
    }

    fun immutableCopy(): ImmutableTypeUniverse {
        return ImmutableTypeUniverse(ImmutableMap.copyOf(typesBySimpleName), ImmutableMap.copyOf(typesBySuperClass))
    }
}