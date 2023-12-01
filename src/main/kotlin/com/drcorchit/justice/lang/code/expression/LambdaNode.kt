package com.drcorchit.justice.lang.code.expression


import com.drcorchit.justice.exceptions.TypeException
import com.drcorchit.justice.game.evaluation.context.DryRunContext
import com.drcorchit.justice.game.evaluation.context.ExecutionContext
import com.drcorchit.justice.game.evaluation.context.StackDryRunContext
import com.drcorchit.justice.game.evaluation.context.StackExecutionContext
import com.drcorchit.justice.lang.code.Code
import com.drcorchit.justice.lang.code.Lambda
import com.drcorchit.justice.lang.code.Thing
import com.drcorchit.justice.lang.environment.Parameters
import com.drcorchit.justice.lang.types.Type

class LambdaNode(val parameters: Parameters, val returnType: Type<*>?, val code: Code) : Expression {
    private var type: Type<*>? = null

    override fun run(context: ExecutionContext): Thing<Lambda> {
        return Lambda(parameters, type ?: returnType!!) {
            val newContext = StackExecutionContext(context.universe, context.sideEffectsDisabled)
            newContext.push(parameters.bind(it))
            code.run(newContext).value
        }.wrap()
    }

    override fun dryRun(context: DryRunContext): Type<*> {
        val newContext = StackDryRunContext(context.universe, context.sideEffectsDisabled)
        newContext.push(parameters.mutableCopy())
        val actualType = code.dryRun(newContext)
        type = if (returnType == null) actualType
        else if (!returnType.accept(actualType)) {
            throw TypeException("lambda", returnType, actualType)
        } else returnType

        return Lambda.getLambdaEvaluator(parameters, type!!)
    }
}