package com.drcorchit.justice.lang.types

import com.drcorchit.justice.lang.members.LambdaFieldMember
import com.drcorchit.justice.lang.members.Member
import com.drcorchit.justice.lang.types.Type.Companion.toMemberMap
import com.drcorchit.justice.lang.types.primitives.BooleanType
import com.google.common.collect.ImmutableMap

class IterableType(val itemType: Type<*>) : Type<Iterable<*>> {
    val iteratorType = IteratorType(itemType)

    override val clazz = Iterable::class.java
    override val members: ImmutableMap<String, Member<Iterable<*>>> = listOf(
        LambdaFieldMember(
            Iterable::class.java,
            "iterator",
            "Returns an iterator over the collection's elements.",
            iteratorType
        ) { it.iterator() }
    ).toMemberMap()

    class IteratorType(val itemType: Type<*>) : Type<Iterator<*>> {
        override val clazz = Iterator::class.java
        override val members: ImmutableMap<String, Member<Iterator<*>>> = listOf<Member<Iterator<*>>>(
            LambdaFieldMember(
                clazz,
                "next",
                "Returns the next item in the iterator.",
                itemType
            ) { it.next()!! },
            LambdaFieldMember(
                clazz,
                "hasNext",
                "Returns true iff the iterator has another item.",
                BooleanType
            ) { it.hasNext() }
        ).associateBy { it.name }.let { ImmutableMap.copyOf(it) }
    }
}