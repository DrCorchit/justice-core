package com.drcorchit.justice.lang.members

import com.drcorchit.justice.game.evaluation.TypeUniverse
import com.drcorchit.justice.lang.annotations.CachedField
import com.drcorchit.justice.utils.Utils.createCache
import com.google.common.cache.LoadingCache
import kotlin.reflect.KCallable

class CachedReflectionMember<T : Any>(
    types: TypeUniverse,
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

    override val cache: LoadingCache<T, Any> = createCache(1000) { apply(it, listOf()) }

    init {
        require(argTypes.size == 1) { "Member $name is marked as a field, but has one or more arguments." }
    }
}