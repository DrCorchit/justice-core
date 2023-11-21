package com.drcorchit.justice.lang.expression

import com.drcorchit.justice.exceptions.TypeException
import com.drcorchit.justice.exceptions.UnrecognizedBinaryOpException
import com.drcorchit.justice.game.evaluation.DryRunContext
import com.drcorchit.justice.game.evaluation.EvaluationContext
import com.drcorchit.justice.lang.evaluators.BooleanEvaluator
import com.drcorchit.justice.lang.evaluators.Evaluator
import com.drcorchit.justice.lang.evaluators.NumberEvaluator
import com.drcorchit.justice.utils.math.MathUtils
import kotlin.math.pow

data class BinaryNode(val left: Expression, val right: Expression, val op: BinaryOp) : Expression {

    override fun evaluate(context: EvaluationContext): Any {
        return op.apply({ left.evaluate(context)!! }, { right.evaluate(context)!! })
    }

    override fun dryRun(context: DryRunContext): Evaluator<*> {
        val leftType = left.dryRun(context)
        if (!op.expectedType.accept(leftType)) {
            throw TypeException(op.symbol, op.expectedType, leftType)
        }

        val rightType = right.dryRun(context)
        if (!op.expectedType.accept(rightType)) {
            throw TypeException(op.symbol, op.expectedType, leftType)
        }

        return op.returnType
    }

    sealed class BinaryOp(val symbol: String, val returnType: Evaluator<*>, val expectedType: Evaluator<*>) {
        abstract fun apply(left: () -> Any, right: () -> Any): Any
    }

    sealed class BooleanOp<T>(symbol: String, expectedType: Evaluator<*>, private val transform: (Any) -> T) : BinaryOp(
        symbol, BooleanEvaluator,
        expectedType
    ) {

        override fun apply(left: () -> Any, right: () -> Any): Boolean {
            return apply2({ transform.invoke(left.invoke()) }, { transform.invoke(right.invoke()) })
        }

        abstract fun apply2(left: () -> T, right: () -> T): Boolean
    }

    sealed class ArithmeticOp(symbol: String) : BinaryOp(symbol, NumberEvaluator, NumberEvaluator) {
        override fun apply(left: () -> Any, right: () -> Any): Any {
            val v1 = left.invoke()
            val v2 = right.invoke()
            return if (v1 is Long && v2 is Long) {
                applyInt(v1, v2)
            } else if (v1 is Number && v2 is Number) {
                applyReal(v1.toDouble(), v2.toDouble())
            } else {
                throw IllegalArgumentException("Cannot apply binary operator to non-numerical types")
            }
        }

        abstract fun applyInt(left: Long, right: Long): Long
        abstract fun applyReal(left: Double, right: Double): Double
    }

    data object Or : BooleanOp<Boolean>("||", BooleanEvaluator, { it as Boolean }) {
        override fun apply2(left: () -> Boolean, right: () -> Boolean): Boolean {
            return left.invoke() || right.invoke()
        }
    }

    data object And : BooleanOp<Boolean>("&&", BooleanEvaluator, { it as Boolean }) {
        override fun apply2(left: () -> Boolean, right: () -> Boolean): Boolean {
            return left.invoke() && right.invoke()
        }
    }

    data object Eq : BooleanOp<Double>("==", NumberEvaluator, { (it as Number).toDouble() }) {
        override fun apply2(left: () -> Double, right: () -> Double): Boolean {
            return left.invoke() == right.invoke()
        }
    }

    data object Neq : BooleanOp<Double>("!=", NumberEvaluator, { (it as Number).toDouble() }) {
        override fun apply2(left: () -> Double, right: () -> Double): Boolean {
            return left.invoke() != right.invoke()
        }
    }

    data object GT : BooleanOp<Double>(">", NumberEvaluator, { (it as Number).toDouble() }) {
        override fun apply2(left: () -> Double, right: () -> Double): Boolean {
            return left.invoke() > right.invoke()
        }
    }

    data object GTE : BooleanOp<Double>(">=", NumberEvaluator, { (it as Number).toDouble() }) {
        override fun apply2(left: () -> Double, right: () -> Double): Boolean {
            return left.invoke() >= right.invoke()
        }
    }

    data object LT : BooleanOp<Double>("<", NumberEvaluator, { (it as Number).toDouble() }) {
        override fun apply2(left: () -> Double, right: () -> Double): Boolean {
            return left.invoke() < right.invoke()
        }
    }

    data object LTE : BooleanOp<Double>("<=", NumberEvaluator, { (it as Number).toDouble() }) {
        override fun apply2(left: () -> Double, right: () -> Double): Boolean {
            return left.invoke() <= right.invoke()
        }
    }

    data object Add : ArithmeticOp("+") {
        override fun applyInt(left: Long, right: Long): Long {
            return left + right
        }

        override fun applyReal(left: Double, right: Double): Double {
            return left + right
        }
    }

    data object Sub : ArithmeticOp("-") {
        override fun applyInt(left: Long, right: Long): Long {
            return left - right
        }

        override fun applyReal(left: Double, right: Double): Double {
            return left - right
        }
    }

    data object Mult : ArithmeticOp("*") {
        override fun applyInt(left: Long, right: Long): Long {
            return left * right
        }

        override fun applyReal(left: Double, right: Double): Double {
            return left * right
        }
    }

    data object Div : ArithmeticOp("/") {
        override fun applyInt(left: Long, right: Long): Long {
            return left / right
        }

        override fun applyReal(left: Double, right: Double): Double {
            return left / right
        }
    }

    data object Mod : ArithmeticOp("%") {
        override fun applyInt(left: Long, right: Long): Long {
            return MathUtils.modulus(left, right)
        }

        override fun applyReal(left: Double, right: Double): Double {
            return MathUtils.modulus(left.toLong(), right.toLong()).toDouble()
        }
    }

    data object Pow : ArithmeticOp("^") {
        override fun applyInt(left: Long, right: Long): Long {
            return left.toDouble().pow(right.toDouble()).toLong()
        }

        override fun applyReal(left: Double, right: Double): Double {
            return left.pow(right)
        }
    }

    companion object {
        fun parse(token: String): BinaryOp {
            return when (token) {
                "||" -> Or
                "&&" -> And
                "==" -> Eq
                "!=" -> Neq
                ">" -> GT
                ">=" -> GTE
                "<" -> LT
                "<=" -> LTE
                "+" -> Add
                "-" -> Sub
                "*" -> Mult
                "/" -> Div
                "%" -> Mod
                "^" -> Pow
                else -> throw UnrecognizedBinaryOpException(token)
            }
        }
    }
}