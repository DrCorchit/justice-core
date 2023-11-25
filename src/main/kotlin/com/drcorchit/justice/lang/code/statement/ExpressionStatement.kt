package com.drcorchit.justice.lang.code.statement

import com.drcorchit.justice.game.evaluation.DryRunContext
import com.drcorchit.justice.game.evaluation.ExecutionContext
import com.drcorchit.justice.lang.code.expression.Expression
import com.drcorchit.justice.lang.types.Type
import com.drcorchit.justice.lang.code.Thing

class ExpressionStatement(val expr: Expression) : Statement {
    override fun run(context: ExecutionContext): Thing<*> {
        return expr.run(context)
    }

    override fun dryRun(context: DryRunContext): Type<*> {
        return expr.dryRun(context)
    }
}