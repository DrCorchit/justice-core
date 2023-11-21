package com.drcorchit.justice.lang.expression

import com.drcorchit.justice.exceptions.TypeException
import com.drcorchit.justice.exceptions.UnrecognizedUnaryOpException
import com.drcorchit.justice.game.evaluation.DryRunContext
import com.drcorchit.justice.game.evaluation.EvaluationContext
import com.drcorchit.justice.lang.evaluators.BooleanEvaluator
import com.drcorchit.justice.lang.evaluators.Evaluator
import com.drcorchit.justice.lang.evaluators.NumberEvaluator

data class UnaryNode(val expr: Expression, val op: UnaryOp) : Expression {

    override fun evaluate(context: EvaluationContext): Any {
        return op.apply(expr.evaluate(context)!!)
    }

    override fun dryRun(context: DryRunContext): Evaluator<*> {
        val type = expr.dryRun(context)
        if (!op.type.accept(type)) {
            throw TypeException(op.symbol, op.type, type)
        }
        return op.type
    }

    sealed class UnaryOp(val symbol: String, val type: Evaluator<*>) {
        abstract fun apply(thing: Any): Any
    }

    data object Not : UnaryOp("!", BooleanEvaluator) {
        override fun apply(thing: Any): Boolean {
            return !(thing as Boolean)
        }
    }

    data object Minus : UnaryOp("-", NumberEvaluator) {
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