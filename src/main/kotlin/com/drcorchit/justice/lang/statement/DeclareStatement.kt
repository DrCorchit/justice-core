package com.drcorchit.justice.lang.statement

import com.drcorchit.justice.exceptions.CompileException
import com.drcorchit.justice.exceptions.TypeException
import com.drcorchit.justice.game.evaluation.DryRunContext
import com.drcorchit.justice.game.evaluation.EvaluationContext
import com.drcorchit.justice.lang.evaluators.Evaluator
import com.drcorchit.justice.lang.expression.Expression
import com.drcorchit.justice.lang.expression.TypeExpression

class DeclareStatement(val id: String, val expr: Expression, val typeExpr: TypeExpression?, val mutable: Boolean) : Statement {
    private lateinit var type: Evaluator<*>

    override fun execute(context: EvaluationContext): Any? {
        check(this::type.isInitialized) { "Cannot assign variable before type checking has been performed" }
        context.env.declare(id, type, expr.evaluate(context), mutable)
        return null
    }

    override fun dryRun(context: DryRunContext): Evaluator<*>? {
        type = if (typeExpr ==null) {
            expr.dryRun(context)
        } else {
            val expectedType = typeExpr.evaluateType(context.game.types)
            val actualType = expr.dryRun(context)
            if (expectedType == null) {
                throw CompileException("Cannot resolve type: ${typeExpr.type}")
            } else if (!expectedType.accept(actualType)) {
                throw TypeException("id", expectedType, actualType)
            }
            expectedType
        }
        return null
    }


}
