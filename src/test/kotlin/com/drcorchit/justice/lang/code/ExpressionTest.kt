package com.drcorchit.justice.lang.code

import com.drcorchit.justice.game.evaluation.context.StackDryRunContext
import com.drcorchit.justice.game.evaluation.context.StackExecutionContext
import com.drcorchit.justice.game.evaluation.universe.TypeUniverse
import com.drcorchit.justice.lang.code.expression.Expression
import org.junit.jupiter.api.Assertions
import kotlin.test.Test

class ExpressionTest {

    @Test
    fun arithmeticTest() {
        val context = StackExecutionContext(TypeUniverse.getDefault(), false)
        val actuals = listOf(
            "2 + 2",
            "2 - 0.1",
            "2000000L * 3000000L",
            "4 / 5",
            "4.0 / 5",
            "20 / 3",
            "20 % 3",
            "20 % -3",
            "-20 % 3",
            "3 > 2",
            "10 >= 10",
            "9 <= 7",
            "\"Mary\" == \"sheep\"",
            "0 == -0"
        )
        val expecteds = listOf(
            4,
            1.9,
            6000000000000L,
            0,
            .8,
            6,
            2,
            2,
            1,
            true,
            true,
            false,
            false,
            true
        )
        actuals.zip(expecteds).forEach {
            val expr = Expression.parse(TypeUniverse.getDefault(), it.first)
            val actual = expr.run(context).value
            val expected = it.second
            Assertions.assertEquals(expected, actual, "Error in expression: ${it.first}")
        }
    }

    @Test
    fun lambdaTest() {
        val context = StackExecutionContext(TypeUniverse.getDefault(), false)
        val dryRunContext = StackDryRunContext(TypeUniverse.getDefault(), false)
        val actuals = listOf("(() -> 3).invoke()",
            "((x: Int, y: Int) -> x+y).invoke(2,3)")
        val expecteds = listOf(3,
            5)
        actuals.zip(expecteds).forEach {
            val expr = Expression.parse(TypeUniverse.getDefault(), it.first)
            expr.dryRun(dryRunContext)
            val actual = expr.run(context).value
            val expected = it.second
            Assertions.assertEquals(expected, actual, "Error in expression: ${it.first}")
        }
    }
}