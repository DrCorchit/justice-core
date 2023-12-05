package com.drcorchit.justice.lang.code.statement

import com.drcorchit.justice.exceptions.ReturnException
import com.drcorchit.justice.game.evaluation.context.DryRunContext
import com.drcorchit.justice.game.evaluation.context.ExecutionContext
import com.drcorchit.justice.lang.code.Thing
import com.drcorchit.justice.lang.code.expression.Expression
import com.drcorchit.justice.lang.types.Type
import com.drcorchit.justice.lang.types.UnitType

class ReturnStatement(val expr: Expression?) : Statement {
    override fun run(context: ExecutionContext): Thing<*> {
        throw ReturnException(expr?.run(context) ?: Thing.UNIT)
    }

    override fun dryRun(context: DryRunContext): Type<*> {
        return expr?.dryRun(context) ?: UnitType
    }

    override fun toString(): String {
        return if (expr == null) "return;" else "return $expr;"
    }
}
