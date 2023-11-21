package com.drcorchit.justice.lang.expression

import com.drcorchit.justice.game.evaluation.DryRunContext
import com.drcorchit.justice.game.evaluation.EvaluationContext
import com.drcorchit.justice.game.evaluation.Types
import com.drcorchit.justice.lang.evaluators.Evaluator
import com.drcorchit.justice.lang.evaluators.EvaluatorEvaluator

interface TypeNode: Expression {

    val type: String

    fun evaluateType(types: Types): Evaluator<*>?

    override fun evaluate(context: EvaluationContext): Evaluator<*> {
        return evaluateType(context.game.types)!!
    }

    override fun dryRun(context: DryRunContext): Evaluator<*> {
        check(evaluateType(context.game.types) != null) {
            "Unable to resolve type: type"
        }
        return EvaluatorEvaluator
    }

}