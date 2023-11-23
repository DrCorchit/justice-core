package com.drcorchit.justice.lang.statement

import com.drcorchit.justice.exceptions.ReturnException
import com.drcorchit.justice.exceptions.ReturnTypeException
import com.drcorchit.justice.game.evaluation.DryRunContext
import com.drcorchit.justice.game.evaluation.EvaluationContext
import com.drcorchit.justice.lang.expression.Expression
import com.drcorchit.justice.lang.types.Type
import com.drcorchit.justice.lang.types.TypedThing
import com.drcorchit.justice.lang.types.UnitType

class ReturnStatement(val expr: Expression?) : Statement {
    override fun execute(context: EvaluationContext): TypedThing<*> {
        throw ReturnException(expr?.evaluate(context) ?: TypedThing.UNIT)
    }

    override fun dryRun(context: DryRunContext): Type<*> {
        throw ReturnTypeException(expr?.dryRun(context) ?: UnitType)
    }
}
