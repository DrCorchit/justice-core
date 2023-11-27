package com.drcorchit.justice.lang.code.expression

import com.drcorchit.justice.exceptions.TypeException
import com.drcorchit.justice.exceptions.UnrecognizedBinaryOpException
import com.drcorchit.justice.game.evaluation.context.DryRunContext
import com.drcorchit.justice.game.evaluation.context.ExecutionContext
import com.drcorchit.justice.lang.types.AnyType
import com.drcorchit.justice.lang.types.Type
import com.drcorchit.justice.lang.code.Thing
import com.drcorchit.justice.lang.types.primitives.BooleanType
import com.drcorchit.justice.lang.types.primitives.NumberType
import com.drcorchit.justice.utils.math.MathUtils
import kotlin.math.pow

data class BinaryNode(val left: Expression, val right: Expression, val op: BinaryOp) : Expression {

    override fun run(context: ExecutionContext): Thing<*> {
        val value = op.apply({ left.run(context).value }, { right.run(context).value })
        return Thing.wrapPrimitive(value)
    }

    override fun dryRun(context: DryRunContext): Type<*> {
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

    sealed class BinaryOp(val symbol: String, val returnType: Type<*>, val expectedType: Type<*>) {
        abstract fun apply(left: () -> Any, right: () -> Any): Any

        fun illegalOperands(x: Any, y: Any): Nothing {
            throw IllegalArgumentException("Illegal arguments to '$symbol' operator: ${x::class} and ${y::class}")
        }
    }

    sealed class ArithmeticOp(symbol: String) : BinaryOp(symbol, NumberType, NumberType)

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

    data object Add : BinaryOp("+", AnyType, AnyType) {
        override fun apply(left: () -> Any, right: () -> Any): Any {
            val x = left.invoke()
            val y = right.invoke()
            return if (x is Int && y is Int) x + y
            else if (x is Long && y is Long) x + y
            else if (x is Number && y is Number) x.toDouble() + y.toDouble()
            else if (x is String || y is String) x.toString() + y.toString()
            else illegalOperands(x, y)
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