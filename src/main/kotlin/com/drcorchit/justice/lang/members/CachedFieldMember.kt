package com.drcorchit.justice.lang.members

import com.google.common.cache.LoadingCache

interface CachedFieldMember<T : Any>: FieldMember<T> {
    val cache: LoadingCache<T, Any>

    override fun get(instance: T): Any {
        return cache.get(instance)
    }

    fun invalidate(instance: Any) {
        cache.invalidate(instance)
    }

    fun invalidateAll() {
        cache.invalidateAll()
    }
}