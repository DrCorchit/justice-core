package com.drcorchit.justice.lang.types

import com.drcorchit.justice.lang.annotations.DerivedField
import com.drcorchit.justice.lang.members.*
import com.drcorchit.justice.lang.types.source.TypeSource
import com.google.common.collect.ImmutableMap

object ArrayType : Type<Array<*>> {
    override val clazz = Array::class.java
    override val members: ImmutableMap<String, Member<Array<*>>> = ImmutableMap.copyOf(listOf(
        DerivedReflectionMember(TypeSource.universe, Array::class.java, Array<*>::size, DerivedField("The size of the array")),
        ReflectionMember(TypeSource.universe, Array::class.java, Array<*>::get, "Gets the element at the given index.", false),
        ReflectionMember(TypeSource.universe, Array::class.java, Array<*>::set, "Sets the element at the given index.", true)
    ).associateBy { it.name })
}