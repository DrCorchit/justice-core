package com.drcorchit.justice.lang.statement

import com.drcorchit.justice.exceptions.JusticeRuntimeException
import com.drcorchit.justice.exceptions.TypeException
import com.drcorchit.justice.game.evaluation.DryRunContext
import com.drcorchit.justice.game.evaluation.EvaluationContext
import com.drcorchit.justice.lang.evaluators.BooleanEvaluator
import com.drcorchit.justice.lang.evaluators.Evaluator
import com.drcorchit.justice.lang.evaluators.StringEvaluator
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

    override fun dryRun(context: DryRunContext): Evaluator<*>? {
        if (condition != null) {
            val actualConditionType = condition.dryRun(context)
            if (actualConditionType != BooleanEvaluator) {
                throw TypeException("throw condition", BooleanEvaluator, actualConditionType)
            }
        }
        val actualThrowableType = message.dryRun(context)
        if (actualThrowableType != StringEvaluator) {
            throw TypeException("throw message", StringEvaluator, actualThrowableType)
        }
        return null
    }
}