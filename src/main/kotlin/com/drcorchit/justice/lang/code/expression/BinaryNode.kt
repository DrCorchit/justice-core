package com.drcorchit.justice.lang.code.expression

import com.drcorchit.justice.exceptions.TypeException
import com.drcorchit.justice.exceptions.UnrecognizedBinaryOpException
import com.drcorchit.justice.game.evaluation.context.DryRunContext
import com.drcorchit.justice.game.evaluation.context.ExecutionContext
import com.drcorchit.justice.lang.code.Thing
import com.drcorchit.justice.lang.types.AnyType
import com.drcorchit.justice.lang.types.IterableType
import com.drcorchit.justice.lang.types.Type
import com.drcorchit.justice.lang.types.primitives.*
import com.drcorchit.justice.utils.math.MathUtils
import kotlin.math.pow

data class BinaryNode(val left: Expression, val right: Expression, val op: BinaryOp) : Expression {

    override fun run(context: ExecutionContext): Thing<*> {
        val value = op.apply({ left.run(context).value }, { right.run(context).value })
        return Thing.wrapPrimitive(value)
    }

    override fun dryRun(context: DryRunContext): Type<*> {
        val left = left.dryRun(context)
        val right = right.dryRun(context)
        return op.dryRun(left, right)
    }

    override fun toString(): String {
        return "$left ${op.symbol} $right"
    }

    sealed class BinaryOp(val symbol: String, val returnType: Type<*>, val expectedType: Type<*>) {
        abstract fun apply(left: () -> Any, right: () -> Any): Any

        open fun dryRun(left: Type<*>, right: Type<*>): Type<*> {
            if (!expectedType.accept(left)) {
                throw TypeException(symbol, expectedType, left)
            }

            if (!expectedType.accept(right)) {
                throw TypeException(symbol, expectedType, left)
            }

            return returnType
        }

        fun illegalOperands(x: Any, y: Any): Nothing {
            throw IllegalArgumentException("Illegal arguments to '$symbol' operator: ${x::class} and ${y::class}")
        }
    }

    sealed class ArithmeticOp(symbol: String) : BinaryOp(symbol, NumberType, NumberType) {
        override fun dryRun(left: Type<*>, right: Type<*>): Type<*> {
            if (!expectedType.accept(left)) {
                throw TypeException(symbol, expectedType, left)
            }

            if (!expectedType.accept(right)) {
                throw TypeException(symbol, expectedType, left)
            }

            return if (left == RealType || right == RealType) {
                RealType
            } else if (left == LongType || right == LongType) {
                LongType
            } else {
                IntType
            }
        }
    }

    data object Or : BinaryOp("||", BooleanType, BooleanType) {
        override fun apply(left: () -> Any, right: () -> Any): Any {
            return left.invoke() as Boolean || right.invoke() as Boolean
        }
    }

    data object And : BinaryOp("&&", BooleanType, BooleanType) {
        override fun apply(left: () -> Any, right: () -> Any): Any {
            return (left.invoke() as Boolean && right.invoke() as Boolean)
        }
    }

    data object Eq : BinaryOp("==", BooleanType, AnyType) {
        override fun apply(left: () -> Any, right: () -> Any): Any {
            return left.invoke() == right.invoke()
        }
    }

    data object Neq : BinaryOp("!=", BooleanType, AnyType) {
        override fun apply(left: () -> Any, right: () -> Any): Any {
            return left.invoke() != right.invoke()
        }
    }

    data object GT : BinaryOp(">", BooleanType, NumberType) {
        override fun apply(left: () -> Any, right: () -> Any): Any {
            return (left.invoke() as Number).toDouble() > (right.invoke() as Number).toDouble()
        }
    }

    data object GTE : BinaryOp(">=", BooleanType, NumberType) {
        override fun apply(left: () -> Any, right: () -> Any): Any {
            return (left.invoke() as Number).toDouble() >= (right.invoke() as Number).toDouble()
        }
    }

    data object LT : BinaryOp("<", BooleanType, NumberType) {
        override fun apply(left: () -> Any, right: () -> Any): Any {
            return (left.invoke() as Number).toDouble() < (right.invoke() as Number).toDouble()
        }
    }

    data object LTE : BinaryOp("<=", BooleanType, NumberType) {
        override fun apply(left: () -> Any, right: () -> Any): Any {
            return (left.invoke() as Number).toDouble() <= (right.invoke() as Number).toDouble()
        }
    }

    data object Add : ArithmeticOp("+") {
        override fun apply(left: () -> Any, right: () -> Any): Any {
            val x = left.invoke()
            val y = right.invoke()
            return if (x is Int && y is Int) x + y
            else if (x is Long && y is Int) x + y
            else if (x is Int && y is Long) x + y
            else if (x is Long && y is Long) x + y
            else if (x is Number && y is Number) x.toDouble() + y.toDouble()
            else if (x is String || y is String) x.toString() + y.toString()
            else illegalOperands(x, y)
        }

        override fun dryRun(left: Type<*>, right: Type<*>): Type<*> {
            return if (left == StringType || right == StringType) {
                StringType
            } else {
                super.dryRun(left, right)
            }
        }
    }

    data object Sub : ArithmeticOp("-") {
        override fun apply(left: () -> Any, right: () -> Any): Any {
            val x = left.invoke()
            val y = right.invoke()
            return if (x is Int && y is Int) x - y
            else if (x is Long && y is Long) x - y
            else if (x is Number && y is Number) x.toDouble() - y.toDouble()
            else illegalOperands(x, y)
        }
    }

    data object Mult : ArithmeticOp("*") {
        override fun apply(left: () -> Any, right: () -> Any): Any {
            val x = left.invoke()
            val y = right.invoke()
            return if (x is Int && y is Int) x * y
            else if (x is Long && y is Long) x * y
            else if (x is Number && y is Number) x.toDouble() * y.toDouble()
            else illegalOperands(x, y)
        }
    }

    data object Div : ArithmeticOp("/") {
        override fun apply(left: () -> Any, right: () -> Any): Any {
            val x = left.invoke()
            val y = right.invoke()
            return if (x is Int && y is Int) x / y
            else if (x is Long && y is Long) x / y
            else if (x is Number && y is Number) x.toDouble() / y.toDouble()
            else illegalOperands(x, y)
        }
    }

    data object Mod : ArithmeticOp("%") {
        override fun apply(left: () -> Any, right: () -> Any): Any {
            val x = left.invoke()
            val y = right.invoke()
            return if (x is Int && y is Int) MathUtils.modulus(x, y)
            else if (x is Long && y is Long) MathUtils.modulus(x, y)
            else if (x is Number && y is Number) MathUtils.modulus(x.toLong(), y.toLong())
            else illegalOperands(x, y)
        }
    }

    data object Pow : ArithmeticOp("^") {
        override fun apply(left: () -> Any, right: () -> Any): Any {
            val x = left.invoke()
            val y = right.invoke()
            return if (x is Number && y is Number) {
                val temp = x.toDouble().pow(y.toDouble())
                if (x is Int && y is Int) temp.toInt()
                else if (x is Long && y is Long) temp.toLong()
                else temp
            } else illegalOperands(x, y)
        }
    }

    data object Range : BinaryOp("...", IterableType(IntType), IntType) {
        override fun apply(left: () -> Any, right: () -> Any): Any {
            val x = left.invoke() as Int
            val y = right.invoke() as Int
            return x..y
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