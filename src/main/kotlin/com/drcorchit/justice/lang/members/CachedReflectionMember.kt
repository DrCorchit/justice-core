package com.drcorchit.justice.lang.members

import com.drcorchit.justice.lang.annotations.CachedField
import com.drcorchit.justice.lang.types.source.TypeSource
import com.drcorchit.justice.utils.Utils.createCache
import com.google.common.cache.LoadingCache
import kotlin.reflect.KCallable

class CachedReflectionMember<T : Any>(
    types: TypeSource,
    clazz: Class<T>,
    member: KCallable<*>,
    annotation: CachedField
) :
    ReflectionMember<T>(
        types,
        clazz,
        member,
        annotation.description,
        false
    ), CachedFieldMember<T> {

    override val cache: LoadingCache<T, Any> = createCache(1000) { apply(it, listOf())!! }

    init {
        require(argTypes.size == 1) { "Member $name is marked as a field, but has one or more arguments." }
    }

    override fun apply(instance: T, args: List<Any?>): Any? {
        val temp = (listOf(instance) + args).toTypedArray()
        return member.call(*temp)
    }
}