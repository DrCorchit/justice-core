package com.drcorchit.justice.lang.expression

import com.drcorchit.justice.exceptions.ReturnException
import com.drcorchit.justice.game.evaluation.DryRunContext
import com.drcorchit.justice.game.evaluation.EvaluationContext
import com.drcorchit.justice.game.evaluation.Lambda
import com.drcorchit.justice.lang.environment.ImmutableTypeEnv
import com.drcorchit.justice.lang.statement.Statement
import com.drcorchit.justice.lang.types.Type

sealed class LambdaNode(val parameters: ImmutableTypeEnv, val returnType: Type<*>?) : Expression

class ExpressionLambdaNode(
    parameters: ImmutableTypeEnv,
    returnType: Type<*>?,
    private val expr: Expression
) :
    LambdaNode(parameters, returnType) {
    override fun evaluate(context: EvaluationContext): Any? {
        return expr.evaluate(context)
    }

    override fun dryRun(context: DryRunContext): Type<*> {
        return expr.dryRun(context)
    }
}

class StatementLambdaNode(
    parameters: ImmutableTypeEnv,
    returnType: Type<*>?,
    private val stmt: Statement
) :
    LambdaNode(parameters, returnType) {
    override fun evaluate(context: EvaluationContext): Any {
        return Lambda(parameters, returnType) {
            val env = parameters.bind(it)
            val newContext = EvaluationContext(context.game, env, context.allowMutation)
            try {
                stmt.execute(newContext)
            } catch (e: ReturnException) {
                e.value
            }
        }
    }

    override fun dryRun(context: DryRunContext): Type<*> {
        val newContext = DryRunContext(context.types, parameters)
        stmt.dryRun(newContext)
        return Lambda.getLambdaEvaluator(parameters, returnType)
    }
}