package com.drcorchit.justice.lang.code.expression

import com.drcorchit.justice.exceptions.TypeException
import com.drcorchit.justice.game.evaluation.DryRunContext
import com.drcorchit.justice.game.evaluation.ExecutionContext
import com.drcorchit.justice.lang.types.ArrayType
import com.drcorchit.justice.lang.types.Type
import com.drcorchit.justice.lang.code.Thing
import com.google.common.collect.ImmutableList

class ArrayNode(val type: Type<*>, val exprs: ImmutableList<Expression>) : Expression {
    override fun run(context: ExecutionContext): Thing<Array<*>> {
        val output = java.lang.reflect.Array.newInstance(type.clazz, exprs.size) as Array<Any>
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
}