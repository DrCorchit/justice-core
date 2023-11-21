package com.drcorchit.justice.lang.statement

import com.drcorchit.justice.game.evaluation.DryRunContext
import com.drcorchit.justice.game.evaluation.EvaluationContext
import com.drcorchit.justice.lang.evaluators.Evaluator
import com.google.common.collect.ImmutableList

class SequenceStatement(val statements: ImmutableList<Statement>) : Statement {
    override fun execute(context: EvaluationContext): Any? {
        statements.dropLast(1).forEach { it.execute(context) }
        return statements.last().execute(context)
    }

    override fun dryRun(context: DryRunContext): Evaluator<*>? {
        statements.dropLast(1).forEach { _ -> dryRun(context) }
        return statements.last().dryRun(context)
    }
}