package com.drcorchit.justice.lang.types.source

import com.drcorchit.justice.lang.types.AnyType
import com.drcorchit.justice.lang.types.Type
import com.drcorchit.justice.utils.Utils.createCache
import com.google.common.cache.LoadingCache
import com.google.common.collect.ImmutableMap
import kotlin.reflect.KClass
import kotlin.reflect.full.isSuperclassOf

class MutableTypeSource : TypeSource {
    private val typesBySimpleName: MutableMap<String, KClass<*>> = mutableMapOf()
    private val typesBySuperClass: MutableMap<KClass<*>, (KClass<*>) -> Type<*>> = mutableMapOf()
    private val cache: LoadingCache<KClass<*>, Type<*>> = createCache(1000) { candidateSubclass ->
        //Find the first matching superclass and
        typesBySuperClass.entries.firstOrNull {
            it.key.isSuperclassOf(candidateSubclass)
        }?.value?.invoke(candidateSubclass) ?: AnyType
    }

    fun registerName(simpleName: String, kClass: KClass<*>) {
        typesBySimpleName[simpleName] = kClass
    }

    fun registerType(superclass: KClass<*>, rule: (KClass<*>) -> Type<*>) {
        typesBySuperClass[superclass] = rule
    }

    override fun parseType(name: String): Type<*> {
        val kClass = typesBySimpleName.getOrElse(name) { Class.forName(name).kotlin }
        return getType(kClass, listOf())
    }

    override fun getType(kClass: KClass<*>, args: List<Type<*>>): Type<*> {
        return cache.get(kClass)
    }

    fun immutableCopy(): ImmutableTypeSource {
        return ImmutableTypeSource(ImmutableMap.copyOf(typesBySimpleName), ImmutableMap.copyOf(typesBySuperClass))
    }
}