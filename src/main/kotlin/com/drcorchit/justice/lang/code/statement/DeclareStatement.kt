package com.drcorchit.justice.lang.code.statement

import com.drcorchit.justice.exceptions.TypeException
import com.drcorchit.justice.game.evaluation.DryRunContext
import com.drcorchit.justice.game.evaluation.ExecutionContext
import com.drcorchit.justice.lang.code.expression.Expression
import com.drcorchit.justice.lang.code.expression.TypeExpression
import com.drcorchit.justice.lang.types.Thing
import com.drcorchit.justice.lang.types.Type
import com.drcorchit.justice.lang.types.UnitType

class DeclareStatement(val id: String, val expr: Expression, val typeExpr: TypeExpression?, val mutable: Boolean) :
    Statement {
    private lateinit var type: Type<*>

    override fun run(context: ExecutionContext): Thing<*> {
        check(this::type.isInitialized) { "Cannot assign variable before type checking has been performed" }
        context.declare(id, type, expr.run(context), mutable)
        return Thing.UNIT
    }

    override fun dryRun(context: DryRunContext): Type<*> {
        type = if (typeExpr ==null) {
            expr.dryRun(context)
        } else {
            val expectedType = typeExpr.resolveType(context.universe)
            val actualType = expr.dryRun(context)
            if (!expectedType.accept(actualType)) {
                throw TypeException(id, expectedType, actualType)
            }
            expectedType
        }
        return UnitType
    }
}
