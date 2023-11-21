package com.drcorchit.justice.lang.expression

import com.drcorchit.justice.exceptions.TypeException
import com.drcorchit.justice.game.evaluation.DryRunContext
import com.drcorchit.justice.game.evaluation.EvaluationContext
import com.drcorchit.justice.lang.evaluators.ArrayEvaluator
import com.drcorchit.justice.lang.evaluators.Evaluator
import com.google.common.collect.ImmutableList

class ArrayNode(val type: Evaluator<*>, val exprs: ImmutableList<Expression>) : Expression {
    override fun evaluate(context: EvaluationContext): Any {
        val output = java.lang.reflect.Array.newInstance(type.clazz.java, exprs.size) as Array<Any>
        exprs.map { it.evaluate(context) }.forEachIndexed { index, value -> output[index] = value!! }
        return output
    }

    override fun dryRun(context: DryRunContext): Evaluator<*> {
        exprs.forEach {
            val actual = it.dryRun(context)
            if (!type.accept(actual)) {
                throw TypeException("array", type, actual)
            }
        }

        return ArrayEvaluator(type)
    }
}