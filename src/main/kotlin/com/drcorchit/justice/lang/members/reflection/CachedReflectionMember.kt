package com.drcorchit.justice.lang.members.reflection

import com.drcorchit.justice.game.evaluation.universe.TypeUniverse
import com.drcorchit.justice.lang.annotations.CachedField
import com.drcorchit.justice.lang.members.CachedFieldMember
import com.drcorchit.justice.lang.types.Type
import com.drcorchit.justice.utils.Utils.createCache
import com.google.common.cache.LoadingCache
import kotlin.reflect.KCallable

class CachedReflectionMember<T : Any>(
    types: TypeUniverse,
    type: Type<in T>,
    member: KCallable<*>,
    annotation: CachedField
) :
    ReflectionMember<T>(
        types,
        type,
        member,
        annotation.description,
        false
    ), CachedFieldMember<T> {

    override val cache: LoadingCache<T, Any> = createCache(1000) { apply(it, listOf()) }

    init {
        require(parameters.entries.isEmpty()) { "Member $name is marked as a field, but has one or more arguments." }
    }
}