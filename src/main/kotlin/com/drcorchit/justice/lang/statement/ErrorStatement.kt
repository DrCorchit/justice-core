package com.drcorchit.justice.lang.statement

import com.drcorchit.justice.exceptions.JusticeRuntimeException
import com.drcorchit.justice.exceptions.TypeException
import com.drcorchit.justice.game.evaluation.DryRunContext
import com.drcorchit.justice.game.evaluation.EvaluationContext
import com.drcorchit.justice.lang.types.primitives.BooleanType
import com.drcorchit.justice.lang.types.Type
import com.drcorchit.justice.lang.types.primitives.StringType
import com.drcorchit.justice.lang.expression.Expression

class ErrorStatement(private val condition: Expression?, private val message: Expression) : Statement {
    override fun execute(context: EvaluationContext): Any? {
        val result = condition?.evaluate(context) as? Boolean ?: true
        if (result) {
            val msg = message.evaluate(context) as String
            throw JusticeRuntimeException(msg)
        }
        return null
    }

    override fun dryRun(context: DryRunContext): Type<*>? {
        if (condition != null) {
            val actualConditionType = condition.dryRun(context)
            if (actualConditionType != BooleanType) {
                throw TypeException("throw condition", BooleanType, actualConditionType)
            }
        }
        val actualThrowableType = message.dryRun(context)
        if (actualThrowableType != StringType) {
            throw TypeException("throw message", StringType, actualThrowableType)
        }
        return null
    }
}