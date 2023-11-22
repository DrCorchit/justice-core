package com.drcorchit.justice.lang.statement

import com.drcorchit.justice.game.evaluation.DryRunContext
import com.drcorchit.justice.game.evaluation.EvaluationContext
import com.drcorchit.justice.lang.evaluators.Evaluator
import com.drcorchit.justice.lang.expression.Expression

class ExpressionStatement(val expr: Expression) : Statement {
    override fun execute(context: EvaluationContext): Any? {
        return expr.evaluate(context)
    }

    override fun dryRun(context: DryRunContext): Evaluator<*> {
        return expr.dryRun(context)
    }
}