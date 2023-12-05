package com.drcorchit.justice.lang.code.statement

import com.drcorchit.justice.game.evaluation.context.DryRunContext
import com.drcorchit.justice.game.evaluation.context.ExecutionContext
import com.drcorchit.justice.lang.code.Thing
import com.drcorchit.justice.lang.code.expression.Expression
import com.drcorchit.justice.lang.types.Type

class ExpressionStatement(val expr: Expression) : Statement {
    override fun run(context: ExecutionContext): Thing<*> {
        return expr.run(context)
    }

    override fun dryRun(context: DryRunContext): Type<*> {
        return expr.dryRun(context)
    }

    override fun toString(): String {
        return "$expr;"
    }
}