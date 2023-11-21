package com.drcorchit.justice.lang.evaluators

import com.drcorchit.justice.lang.members.LambdaFieldMember
import com.drcorchit.justice.lang.members.LambdaMember
import com.drcorchit.justice.lang.members.Member
import com.google.common.collect.ImmutableMap

class ArrayEvaluator(val type: Evaluator<*>) : Evaluator<Array<*>> {
    override val clazz = Array::class

    override val members: ImmutableMap<String, Member<Array<*>>> = ImmutableMap.copyOf(listOf(
        LambdaFieldMember<Array<*>>(
            "size",
            "The size of the array.",
            IntEvaluator
        ) {
            it.size
        },
        LambdaMember(
            "get",
            "Retrieves the element at the given index.",
            listOf(IntEvaluator),
            type
        ) { instance, args ->
            instance[args.first() as Int]
        },
        LambdaMember(
            "set",
            "Set the element at the given index.",
            listOf(IntEvaluator, type),
            type
        ) { instance, args ->
            val index = args.first() as Int
            val newValue = args.last()
            val oldValue = instance[index]
            (instance[index] as Array<Any>)[index] = type.cast(newValue)
            oldValue
        }
    ).associateBy { it.name })
}