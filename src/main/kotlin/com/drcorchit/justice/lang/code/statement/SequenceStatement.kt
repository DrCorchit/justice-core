package com.drcorchit.justice.lang.code.statement

import com.drcorchit.justice.game.evaluation.context.DryRunContext
import com.drcorchit.justice.game.evaluation.context.ExecutionContext
import com.drcorchit.justice.lang.code.Thing
import com.drcorchit.justice.lang.types.Type
import com.google.common.collect.ImmutableList

class SequenceStatement(val statements: ImmutableList<Statement>) : Statement {
    override fun run(context: ExecutionContext): Thing<*> {
        statements.dropLast(1).forEach { it.run(context) }
        return statements.last().run(context)
    }

    override fun dryRun(context: DryRunContext): Type<*> {
        statements.dropLast(1).forEach { it.dryRun(context) }
        return statements.last().dryRun(context)
    }

    override fun toString(): String {
        return statements.joinToString("\n")
    }
}