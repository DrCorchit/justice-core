package com.drcorchit.justice.lang.types

import com.drcorchit.justice.lang.members.Member
import com.drcorchit.justice.lang.members.lambda.LambdaFieldMember
import com.drcorchit.justice.lang.types.primitives.BooleanType
import com.google.common.collect.ImmutableMap

class IterableType(val itemType: Type<*>) : Type<Iterable<*>>(Iterable::class) {
    val iteratorType = IteratorType(itemType)

    override val members: ImmutableMap<String, Member<Iterable<*>>> = listOf(
        LambdaFieldMember(
            this,
            "iterator",
            "Returns an iterator over the collection's elements.",
            iteratorType
        ) { it.iterator() }
    ).toMemberMap()

    class IteratorType(val itemType: Type<*>) : Type<Iterator<*>>(Iterator::class) {
        override val members: ImmutableMap<String, Member<Iterator<*>>> = listOf<Member<Iterator<*>>>(
            LambdaFieldMember(
                this,
                "next",
                "Returns the next item in the iterator.",
                itemType
            ) { it.next()!! },
            LambdaFieldMember(
                this,
                "hasNext",
                "Returns true iff the iterator has another item.",
                BooleanType
            ) { it.hasNext() }
        ).associateBy { it.name }.let { ImmutableMap.copyOf(it) }
    }
}