package com.drcorchit.justice.lang.evaluators

import com.drcorchit.justice.lang.members.LambdaFieldMember
import com.drcorchit.justice.lang.members.Member
import com.google.common.collect.ImmutableMap

object EvaluatorEvaluator : NonSerializableEvaluator<Evaluator<*>>() {
    override val clazz = Evaluator::class
    override val members: ImmutableMap<String, Member<Evaluator<*>>> = ImmutableMap.copyOf(listOf(
        LambdaFieldMember<Evaluator<*>>(
            "name",
            "The name of the type",
            StringEvaluator
        ) { it.clazz.qualifiedName }
    ).associateBy { it.name })
}