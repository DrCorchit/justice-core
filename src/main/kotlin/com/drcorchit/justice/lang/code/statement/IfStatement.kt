package com.drcorchit.justice.lang.code.statement

import com.drcorchit.justice.exceptions.TypeException
import com.drcorchit.justice.game.evaluation.context.DryRunContext
import com.drcorchit.justice.game.evaluation.context.ExecutionContext
import com.drcorchit.justice.lang.code.Thing
import com.drcorchit.justice.lang.code.expression.Expression
import com.drcorchit.justice.lang.types.Type
import com.drcorchit.justice.lang.types.UnitType
import com.drcorchit.justice.lang.types.primitives.BooleanType

class IfStatement(val condition: Expression, val ifClause: Statement, val elseClause: Statement?) :
    Statement {
    override fun run(context: ExecutionContext): Thing<*> {
        val result = condition.run(context).value as Boolean
        return if (result) {
            context.push()
            val value = ifClause.run(context)
            context.pop()
            value
        } else if (elseClause != null) {
            context.push()
            val value = elseClause.run(context)
            context.pop()
            value
        } else {
            Thing.UNIT
        }
    }

    override fun dryRun(context: DryRunContext): Type<*> {
        val conditionType = condition.dryRun(context)
        if (conditionType != BooleanType) {
            throw TypeException("if condition", BooleanType, conditionType)
        }
        ifClause.dryRun(context)
        elseClause?.dryRun(context)
        return UnitType
        //TODO return the correct value. if and else should return the same value if present.
        //If else is absent, the value of the statement is null.
        //Finally, elseClause dryRun should run even if ifClause throws a ReturnTypeException.
    }

    override fun toString(): String {
        val ifSection = "if ($condition) {\n$ifClause\n}"
        return when (elseClause) {
            null -> ifSection
            is ForStatement,
            is SequenceStatement,
            is WhileStatement -> "$ifSection else {\n$elseClause\n}"
            else -> "$ifSection else $elseClause"
        }
    }
}
