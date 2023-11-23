package com.drcorchit.justice.lang.statement

import com.drcorchit.justice.exceptions.TypeException
import com.drcorchit.justice.game.evaluation.DryRunContext
import com.drcorchit.justice.game.evaluation.EvaluationContext
import com.drcorchit.justice.lang.types.primitives.BooleanType
import com.drcorchit.justice.lang.types.Type
import com.drcorchit.justice.lang.expression.Expression

class IfStatement(val condition: Expression, val ifClause: Statement, val elseClause: Statement?) :
    Statement {
    override fun execute(context: EvaluationContext): Any? {
        val result = condition.evaluate(context) as Boolean
        return if (result) {
            ifClause.execute(context)
        } else {
            elseClause?.execute(context)
        }
    }

    override fun dryRun(context: DryRunContext): Type<*>? {
        val conditionType = condition.dryRun(context)
        if (conditionType != BooleanType) {
            throw TypeException("if condition", BooleanType, conditionType)
        }
        ifClause.dryRun(context)
        elseClause?.dryRun(context)
        return null
        //TODO return the correct value. if and else should return the same value if present.
        //If else is absent, the value of the statement is null.
        //Finally, elseClause dryRun should run even if ifClause throws a ReturnTypeException.
    }
}
