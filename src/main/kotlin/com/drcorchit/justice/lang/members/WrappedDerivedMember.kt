package com.drcorchit.justice.lang.members

import com.drcorchit.justice.lang.annotations.DerivedField
import kotlin.reflect.KCallable

class WrappedDerivedMember<T>(member: KCallable<*>, annotation: DerivedField) :
    WrappedMember<T>(member, annotation.description), FieldMember<T> {
    init {
        require(argTypes.size == 1) { "Member $name is marked as a field, but has one or more arguments." }
    }

    override fun get(instance: T): Any? {
        return apply(instance, listOf())
    }
}