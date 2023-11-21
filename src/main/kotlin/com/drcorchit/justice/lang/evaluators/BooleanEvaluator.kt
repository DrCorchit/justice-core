package com.drcorchit.justice.lang.evaluators

import com.drcorchit.justice.lang.members.Member
import com.google.common.collect.ImmutableMap

object BooleanEvaluator: Evaluator<Boolean> {
    override val clazz = Boolean::class
    override val members: ImmutableMap<String, Member<Boolean>> = ImmutableMap.of()
}