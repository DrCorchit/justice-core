package com.drcorchit.justice.lang.members

import com.drcorchit.justice.lang.annotations.DerivedField
import com.drcorchit.justice.lang.types.source.TypeSource
import kotlin.reflect.KCallable

class DerivedReflectionMember<T : Any>(types: TypeSource, clazz: Class<T>, member: KCallable<*>, annotation: DerivedField) :
    ReflectionMember<T>(types, clazz, member, annotation.description, false), FieldMember<T> {
    init {
        require(argTypes.size == 1) { "Member $name is marked as a field, but has one or more arguments." }
    }
}