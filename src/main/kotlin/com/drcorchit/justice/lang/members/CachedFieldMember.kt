package com.drcorchit.justice.lang.members

import com.drcorchit.justice.lang.evaluators.Evaluator
import com.drcorchit.justice.utils.Utils.createCache
import com.google.common.collect.ImmutableList

abstract class CachedFieldMember<T : Any>(name: String, description: String, returnType: Evaluator<*>) :
    AbstractMember<T>(name, description, ImmutableList.of(), returnType), FieldMember<T> {

    val cache = createCache<T, Any>(500) {
        apply(it, listOf())!!
    }

    override fun get(instance: T): Any? {
        return cache.getUnchecked(instance)
    }

    fun invalidate(instance: Any) {
        cache.invalidate(instance)
    }

    fun invalidateAll() {
        cache.invalidateAll()
    }
}