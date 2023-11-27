package com.drcorchit.justice.lang.code.statement

import com.drcorchit.justice.exceptions.JusticeRuntimeException
import com.drcorchit.justice.exceptions.TypeException
import com.drcorchit.justice.game.evaluation.context.DryRunContext
import com.drcorchit.justice.game.evaluation.context.ExecutionContext
import com.drcorchit.justice.lang.code.Thing
import com.drcorchit.justice.lang.code.expression.Expression
import com.drcorchit.justice.lang.types.Type
import com.drcorchit.justice.lang.types.UnitType
import com.drcorchit.justice.lang.types.primitives.BooleanType
import com.drcorchit.justice.lang.types.primitives.StringType

class ErrorStatement(private val condition: Expression?, private val message: Expression) : Statement {
    override fun run(context: ExecutionContext): Thing<*> {
        val result = condition?.run(context)?.value as? Boolean ?: true
        if (result) {
            val msg = message.run(context).value as String
            throw JusticeRuntimeException(msg)
        }
        return Thing.UNIT
    }

    override fun dryRun(context: DryRunContext): Type<*> {
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
        return UnitType
    }

    override fun toString(): String {
        return if (condition == null) {
            "throw $message"
        } else {
            "$condition throws $message"
        }
    }
}