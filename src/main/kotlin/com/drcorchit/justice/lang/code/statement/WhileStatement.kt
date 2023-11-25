package com.drcorchit.justice.lang.code.statement

import com.drcorchit.justice.exceptions.TypeException
import com.drcorchit.justice.game.evaluation.DryRunContext
import com.drcorchit.justice.game.evaluation.ExecutionContext
import com.drcorchit.justice.lang.code.expression.Expression
import com.drcorchit.justice.lang.types.Thing
import com.drcorchit.justice.lang.types.Type
import com.drcorchit.justice.lang.types.UnitType
import com.drcorchit.justice.lang.types.primitives.BooleanType

class WhileStatement(val condition: Expression, val loop: Statement): Statement {
    override fun run(context: ExecutionContext): Thing<*> {

        while (condition.run(context).value as Boolean) {
            context.push()
            loop.run(context)
            context.pop()
        }
        return Thing.UNIT
    }

    override fun dryRun(context: DryRunContext): Type<*> {
        val actualType = condition.dryRun(context)
        if (actualType != BooleanType) {
            throw TypeException("while", BooleanType, actualType)
        }
        context.push()
        loop.dryRun(context)
        context.pop()
        return UnitType
    }
}