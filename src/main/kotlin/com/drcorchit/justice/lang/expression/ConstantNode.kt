package com.drcorchit.justice.lang.expression

import com.drcorchit.justice.game.evaluation.DryRunContext
import com.drcorchit.justice.game.evaluation.EvaluationContext
import com.drcorchit.justice.lang.types.Type

class ConstantNode (val value: Any): Expression {
    companion object {
        val TRUE = ConstantNode(true)
        val FALSE = ConstantNode(false)
        val PI = ConstantNode(Math.PI)
        val E = ConstantNode(Math.E)
    }

    override fun evaluate(context: EvaluationContext): Any {
        return value
    }

    override fun dryRun(context: DryRunContext): Type<*> {
        return context.types.typeOfInstance(value)
    }
}