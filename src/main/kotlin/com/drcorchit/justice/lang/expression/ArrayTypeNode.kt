package com.drcorchit.justice.lang.expression

import com.drcorchit.justice.game.evaluation.Types
import com.drcorchit.justice.lang.evaluators.ArrayEvaluator
import com.drcorchit.justice.lang.evaluators.Evaluator

class ArrayTypeNode(private val eleType: TypeNode) : TypeNode {
    override val type = eleType.type + "[]"

    override fun evaluateType(types: Types): Evaluator<*>? {
        val eleType = eleType.evaluateType(types)
        return if (eleType == null) null else ArrayEvaluator(eleType)
    }
}