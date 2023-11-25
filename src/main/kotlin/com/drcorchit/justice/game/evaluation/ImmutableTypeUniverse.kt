package com.drcorchit.justice.game.evaluation

import com.drcorchit.justice.lang.types.ReflectionType
import com.drcorchit.justice.lang.types.Type
import com.drcorchit.justice.utils.Utils
import com.google.common.cache.LoadingCache
import com.google.common.collect.ImmutableMap
import kotlin.reflect.KClass
import kotlin.reflect.full.isSuperclassOf

class ImmutableTypeUniverse(
    private val typesBySimpleName: ImmutableMap<String, KClass<*>>,
    private val typesBySuperClass: ImmutableMap<KClass<*>, (TypeParameters) -> Type<*>>
) : TypeUniverse {
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

    fun mutableCopy(): MutableTypeUniverse {
        val output = MutableTypeUniverse()
        typesBySimpleName.forEach { output.registerName(it.key, it.value) }
        typesBySuperClass.forEach { output.registerType(it.key, it.value) }
        return output
    }
}