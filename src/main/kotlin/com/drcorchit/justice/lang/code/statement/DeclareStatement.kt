package com.drcorchit.justice.lang.code.statement

import com.drcorchit.justice.exceptions.TypeException
import com.drcorchit.justice.game.evaluation.context.DryRunContext
import com.drcorchit.justice.game.evaluation.context.ExecutionContext
import com.drcorchit.justice.lang.code.Thing
import com.drcorchit.justice.lang.code.expression.Expression
import com.drcorchit.justice.lang.code.expression.TypeExpression
import com.drcorchit.justice.lang.types.Type
import com.drcorchit.justice.lang.types.UnitType

class DeclareStatement(private val id: String, private val newValue: Expression, private val typeExpr: TypeExpression?, val mutable: Boolean) :
    Statement {
    private lateinit var type: Type<*>

    override fun run(context: ExecutionContext): Thing<*> {
        check(this::type.isInitialized) { "Cannot assign variable before type checking has been performed" }
        context.declare(id, type, newValue.run(context), mutable)
        return Thing.UNIT
    }

    override fun dryRun(context: DryRunContext): Type<*> {
        type = if (typeExpr ==null) {
            newValue.dryRun(context)
        } else {
            val expectedType = typeExpr.resolveType(context.universe)
            val actualType = newValue.dryRun(context)
            if (!expectedType.accept(actualType)) {
                throw TypeException(id, expectedType, actualType)
            }
            expectedType
        }
        return UnitType
    }

    override fun toString(): String {
        val declarator = if (mutable) "var" else "val"
        val type = if (typeExpr == null) "" else ": $typeExpr "
        return "$declarator $id $type = $newValue;"
    }
}
