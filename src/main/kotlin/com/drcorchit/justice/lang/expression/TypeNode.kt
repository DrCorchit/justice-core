package com.drcorchit.justice.lang.expression

import com.drcorchit.justice.game.evaluation.Types
import com.drcorchit.justice.lang.evaluators.Evaluator

class TypeNode(override val type: String) : TypeExpression {
    override fun evaluateType(types: Types): Evaluator<*>? {
        return types.getType(type)
    }
}