package com.drcorchit.justice.lang.statement

import com.drcorchit.justice.exceptions.TypeException
import com.drcorchit.justice.game.evaluation.DryRunContext
import com.drcorchit.justice.game.evaluation.EvaluationContext
import com.drcorchit.justice.lang.expression.Expression
import com.drcorchit.justice.lang.expression.TypeExpression
import com.drcorchit.justice.lang.types.Type
import com.drcorchit.justice.lang.types.TypedThing
import com.drcorchit.justice.lang.types.UnitType

class DeclareStatement(val id: String, val expr: Expression, val typeExpr: TypeExpression?, val mutable: Boolean) : Statement {
    private lateinit var type: Type<*>

    override fun execute(context: EvaluationContext): TypedThing<*> {
        check(this::type.isInitialized) { "Cannot assign variable before type checking has been performed" }
        context.env.declare(id, type, expr.evaluate(context), mutable)
        return TypedThing.UNIT
    }

    override fun dryRun(context: DryRunContext): Type<*> {
        type = if (typeExpr ==null) {
            expr.dryRun(context)
        } else {
            val expectedType = typeExpr.evaluateType(context.types)
            val actualType = expr.dryRun(context)
            if (!expectedType.accept(actualType)) {
                throw TypeException("id", expectedType, actualType)
            }
            expectedType
        }
        return UnitType
    }
}
