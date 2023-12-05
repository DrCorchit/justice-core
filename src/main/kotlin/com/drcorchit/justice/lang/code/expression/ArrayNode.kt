package com.drcorchit.justice.lang.code.expression

import com.drcorchit.justice.exceptions.TypeException
import com.drcorchit.justice.game.evaluation.context.DryRunContext
import com.drcorchit.justice.game.evaluation.context.ExecutionContext
import com.drcorchit.justice.lang.code.Thing
import com.drcorchit.justice.lang.types.ArrayType
import com.drcorchit.justice.lang.types.Type
import com.google.common.collect.ImmutableList

class ArrayNode(val type: Type<*>, val exprs: ImmutableList<Expression>) : Expression {
    override fun run(context: ExecutionContext): Thing<Array<*>> {
        val output = java.lang.reflect.Array.newInstance(type.clazz.java, exprs.size) as Array<Any>
        exprs.map { it.run(context) }.forEachIndexed { index, value -> output[index] = value.value }
        return ArrayType(type).wrap(output)
    }

    override fun dryRun(context: DryRunContext): Type<*> {
        exprs.forEach {
            val actual = it.dryRun(context)
            if (!type.accept(actual)) {
                throw TypeException("array", type, actual)
            }
        }
        return ArrayType(type)
    }

    override fun toString(): String {
        val values = exprs.joinToString(", ")
        return "$type[$values]"
    }
}