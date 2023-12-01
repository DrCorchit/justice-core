package com.drcorchit.justice.lang.members.reflection

import com.drcorchit.justice.game.evaluation.universe.TypeUniverse
import com.drcorchit.justice.lang.annotations.DerivedField
import com.drcorchit.justice.lang.members.FieldMember
import com.drcorchit.justice.lang.types.Type
import kotlin.reflect.KCallable

class DerivedReflectionMember<T : Any>(types: TypeUniverse, type: Type<in T>, member: KCallable<*>, annotation: DerivedField) :
    ReflectionMember<T>(types, type, member, annotation.description, false), FieldMember<T> {
    init {
        require(parameters.entries.isEmpty()) { "Member $name is marked as a field, but has one or more arguments." }
    }
}