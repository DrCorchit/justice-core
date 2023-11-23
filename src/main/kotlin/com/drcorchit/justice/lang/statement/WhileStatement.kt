package com.drcorchit.justice.lang.statement

import com.drcorchit.justice.exceptions.TypeException
import com.drcorchit.justice.game.evaluation.DryRunContext
import com.drcorchit.justice.game.evaluation.EvaluationContext
import com.drcorchit.justice.lang.types.primitives.BooleanType
import com.drcorchit.justice.lang.types.Type
import com.drcorchit.justice.lang.expression.Expression

class WhileStatement(val condition: Expression, val loop: Statement): Statement {
    override fun execute(context: EvaluationContext): Any? {
        while (condition.evaluate(context) as Boolean) {
            loop.execute(context)
        }
        return null
    }

    override fun dryRun(context: DryRunContext): Type<*>? {
        val actualType = condition.dryRun(context)
        if (actualType != BooleanType) {
            throw TypeException("while", BooleanType, actualType)
        }
        loop.dryRun(context)
        return null
    }
}