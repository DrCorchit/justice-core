package com.drcorchit.justice.lang.expression

import com.drcorchit.justice.exceptions.ReturnException
import com.drcorchit.justice.exceptions.TypeException
import com.drcorchit.justice.game.evaluation.DryRunContext
import com.drcorchit.justice.game.evaluation.EvaluationContext
import com.drcorchit.justice.game.evaluation.Lambda
import com.drcorchit.justice.lang.environment.ImmutableTypeEnv
import com.drcorchit.justice.lang.statement.Statement
import com.drcorchit.justice.lang.types.Type

sealed class LambdaNode(val parameters: ImmutableTypeEnv, val returnType: Type<*>?) : Expression {
    protected var type: Type<*>? = null
}

class ExpressionLambdaNode(
    parameters: ImmutableTypeEnv,
    returnType: Type<*>?,
    private val expr: Expression
) :
    LambdaNode(parameters, returnType) {
    override fun evaluate(context: EvaluationContext): Any {
        return Lambda(parameters, type) {
            //TODO handle env correctly
            val env = parameters.bind(null, it)
            expr.evaluate(EvaluationContext(context.types, env, context.allowMutation))
        }
    }

    override fun dryRun(context: DryRunContext): Type<*> {
        val newContext = DryRunContext(context.types, parameters)
        val actualType = expr.dryRun(newContext)
        if (returnType == null) {
            type = actualType
        } else if (!returnType.accept(actualType)) {
            throw TypeException("lambda", returnType, actualType)
        }
        return Lambda.getLambdaEvaluator(parameters, type)
    }
}

class StatementLambdaNode(
    parameters: ImmutableTypeEnv,
    returnType: Type<*>?,
    private val stmt: Statement
) :
    LambdaNode(parameters, returnType) {
    override fun evaluate(context: EvaluationContext): Lambda {
        return Lambda(parameters, returnType) {
            //TODO handle env correctly
            val env = parameters.bind(null, it)
            val newContext = EvaluationContext(context.types, env, context.allowMutation)
            try {
                stmt.execute(newContext)
            } catch (e: ReturnException) {
                e.value
            }
        }
    }

    override fun dryRun(context: DryRunContext): Type<Lambda> {
        val newContext = DryRunContext(context.types, parameters)
        val actualType = stmt.dryRun(newContext)
        if (returnType == null) {
            type = actualType
        } else if (!returnType.accept(actualType!!)) {
            throw TypeException("lambda", returnType, actualType)
        }
        return Lambda.getLambdaEvaluator(parameters, type)
    }
}