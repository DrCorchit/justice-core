package com.drcorchit.justice.lang.statement

import com.drcorchit.justice.game.evaluation.DryRunContext
import com.drcorchit.justice.game.evaluation.EvaluationContext
import com.drcorchit.justice.lang.types.Type
import com.drcorchit.justice.lang.types.TypedThing
import com.google.common.collect.ImmutableList

class SequenceStatement(val statements: ImmutableList<Statement>) : Statement {
    override fun execute(context: EvaluationContext): TypedThing<*> {
        statements.dropLast(1).forEach { it.execute(context) }
        return statements.last().execute(context)
    }

    override fun dryRun(context: DryRunContext): Type<*> {
        statements.dropLast(1).forEach { _ -> dryRun(context) }
        return statements.last().dryRun(context)
    }
}