package com.drcorchit.justice.lang.code.statement

import com.drcorchit.justice.exceptions.TypeException
import com.drcorchit.justice.game.evaluation.context.DryRunContext
import com.drcorchit.justice.game.evaluation.context.ExecutionContext
import com.drcorchit.justice.lang.code.Thing
import com.drcorchit.justice.lang.code.expression.Expression
import com.drcorchit.justice.lang.types.Type
import com.drcorchit.justice.lang.types.UnitType
import com.drcorchit.justice.lang.types.primitives.BooleanType

class WhileStatement(private val condition: Expression, private val loop: Statement) : Statement {
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

    override fun toString(): String {
        return when (loop) {
            is AssignStatement,
            is ErrorStatement,
            is ExpressionStatement,
            is ReturnStatement -> "while ($condition) $loop;"
            else -> "while ($condition) {\n$loop\n}"
        }
    }
}