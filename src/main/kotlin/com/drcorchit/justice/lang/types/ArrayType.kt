package com.drcorchit.justice.lang.types

import com.drcorchit.justice.game.evaluation.universe.TypeUniverse
import com.drcorchit.justice.lang.annotations.DerivedField
import com.drcorchit.justice.lang.members.*
import com.drcorchit.justice.lang.members.reflection.DerivedReflectionMember
import com.drcorchit.justice.lang.members.reflection.ReflectionMember
import com.google.common.collect.ImmutableMap

class ArrayType(val type: Type<*>, val universe: TypeUniverse = TypeUniverse.getDefault()) : Type<Array<*>>(Array::class) {
    override val members: ImmutableMap<String, Member<Array<*>>> = listOf(
        DerivedReflectionMember(universe, this, Array<*>::size, DerivedField("The size of the array")),
        ReflectionMember(universe, this, Array<*>::get, "Gets the element at the given index.", false),
        ReflectionMember(universe, this, Array<*>::set, "Sets the element at the given index.", true)
    ).toMemberMap()
}