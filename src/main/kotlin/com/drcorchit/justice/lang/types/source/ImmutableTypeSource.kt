package com.drcorchit.justice.lang.types.source

import com.drcorchit.justice.lang.types.AnyType
import com.drcorchit.justice.lang.types.ReflectionType
import com.drcorchit.justice.lang.types.Type
import com.drcorchit.justice.utils.Utils
import com.google.common.cache.LoadingCache
import com.google.common.collect.ImmutableMap
import kotlin.reflect.KClass
import kotlin.reflect.full.isSuperclassOf

class ImmutableTypeSource(
    private val typesBySimpleName: ImmutableMap<String, KClass<*>>,
    private val typesBySuperClass: ImmutableMap<KClass<*>, (KClass<*>) -> Type<*>>
) : TypeSource {
    private val cache: LoadingCache<KClass<*>, Type<*>> = Utils.createCache(1000) { candidateSubclass ->
        typesBySuperClass.entries.firstOrNull {
            it.key.isSuperclassOf(candidateSubclass)
        }?.value?.invoke(candidateSubclass) ?: ReflectionType(this, candidateSubclass)
    }

    override fun parseType(name: String): Type<*> {
        val kClass = typesBySimpleName.getOrElse(name) { Class.forName(name).kotlin }
        return getType(kClass, listOf())
    }

    override fun getType(kClass: KClass<*>, args: List<Type<*>>): Type<*> {
        val temp = cache.get(kClass)
        if (temp == AnyType) {
            println("Unsupported type: ${kClass.qualifiedName}")
        }
        return temp
    }

    fun mutableCopy(): MutableTypeSource {
        val output = MutableTypeSource()
        typesBySimpleName.forEach { output.registerName(it.key, it.value) }
        typesBySuperClass.forEach { output.registerType(it.key, it.value) }
        return output
    }
}