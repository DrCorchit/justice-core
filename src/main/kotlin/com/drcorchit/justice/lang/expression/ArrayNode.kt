package com.drcorchit.justice.lang.expression

import com.drcorchit.justice.exceptions.TypeException
import com.drcorchit.justice.game.evaluation.DryRunContext
import com.drcorchit.justice.game.evaluation.EvaluationContext
import com.drcorchit.justice.lang.types.ArrayType
import com.drcorchit.justice.lang.types.Type
import com.google.common.collect.ImmutableList

class ArrayNode(val type: Type<*>, val exprs: ImmutableList<Expression>) : Expression {
    override fun evaluate(context: EvaluationContext): Any {
        val output = java.lang.reflect.Array.newInstance(type.clazz, exprs.size) as Array<Any>
        exprs.map { it.evaluate(context) }.forEachIndexed { index, value -> output[index] = value!! }
        return output
    }

    override fun dryRun(context: DryRunContext): Type<*> {
        exprs.forEach {
            val actual = it.dryRun(context)
            if (!type.accept(actual)) {
                throw TypeException("array", type, actual)
            }
        }
        return ArrayType
    }
}