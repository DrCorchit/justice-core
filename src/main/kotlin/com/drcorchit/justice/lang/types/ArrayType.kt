package com.drcorchit.justice.lang.types

import com.drcorchit.justice.game.evaluation.TypeUniverse
import com.drcorchit.justice.lang.annotations.DerivedField
import com.drcorchit.justice.lang.members.*
import com.google.common.collect.ImmutableMap

class ArrayType(val type: Type<*>) : Type<Array<*>> {
    override val clazz = Array::class.java
    override val members: ImmutableMap<String, Member<Array<*>>> = ImmutableMap.copyOf(listOf(
        DerivedReflectionMember(TypeUniverse.getDefault(), Array::class.java, Array<*>::size, DerivedField("The size of the array")),
        ReflectionMember(TypeUniverse.getDefault(), Array::class.java, Array<*>::get, "Gets the element at the given index.", false),
        ReflectionMember(TypeUniverse.getDefault(), Array::class.java, Array<*>::set, "Sets the element at the given index.", true)
    ).associateBy { it.name })
}