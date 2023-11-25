package com.drcorchit.justice.lang.code.expression

import com.drcorchit.justice.exceptions.TypeException
import com.drcorchit.justice.exceptions.UnrecognizedUnaryOpException
import com.drcorchit.justice.game.evaluation.DryRunContext
import com.drcorchit.justice.game.evaluation.ExecutionContext
import com.drcorchit.justice.lang.types.Type
import com.drcorchit.justice.lang.code.Thing
import com.drcorchit.justice.lang.types.primitives.BooleanType
import com.drcorchit.justice.lang.types.primitives.NumberType

data class UnaryNode(val expr: Expression, val op: UnaryOp) : Expression {

    override fun run(context: ExecutionContext): Thing<*> {
        val value = op.apply(expr.run(context).value)
        return Thing.wrapPrimitive(value)
    }

    override fun dryRun(context: DryRunContext): Type<*> {
        val type = expr.dryRun(context)
        if (!op.type.accept(type)) {
            throw TypeException(op.symbol, op.type, type)
        }
        return op.type
    }

    sealed class UnaryOp(val symbol: String, val type: Type<*>) {
        abstract fun apply(thing: Any): Any
    }

    data object Not : UnaryOp("!", BooleanType) {
        override fun apply(thing: Any): Boolean {
            return !(thing as Boolean)
        }
    }

    data object Minus : UnaryOp("-", NumberType) {
        override fun apply(thing: Any): Number {
            return when (thing) {
                is Int -> -thing
                is Long -> -thing
                is Number -> -thing.toDouble()
                else -> throw IllegalArgumentException("Cannot apply unary operator minus to non-numerical types.")
            }
        }
    }

    companion object {
        fun parse(op: String): UnaryOp {
            return when(op) {
                "-" -> Minus
                "!" -> Not
                else -> throw UnrecognizedUnaryOpException(op)
            }
        }
    }
}