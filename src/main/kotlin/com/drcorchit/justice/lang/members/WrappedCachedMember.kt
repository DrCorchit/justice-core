package com.drcorchit.justice.lang.members

import com.drcorchit.justice.lang.annotations.CachedField
import com.drcorchit.justice.utils.Utils
import com.google.common.cache.LoadingCache
import kotlin.reflect.KCallable

class WrappedCachedMember<T>(member: KCallable<*>, annotation: CachedField) :
    WrappedMember<T>(member, annotation.description), FieldMember<T> {
    private val cache: LoadingCache<T, Any> = Utils.createCache(annotation.cacheSize) { apply(it, listOf())!! }

    init {
        require(argTypes.size == 1) { "Member $name is marked as a field, but has one or more arguments." }
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