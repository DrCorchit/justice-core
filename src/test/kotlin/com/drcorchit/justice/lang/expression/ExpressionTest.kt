package com.drcorchit.justice.lang.expression

import com.drcorchit.justice.game.evaluation.DryRunContext
import com.drcorchit.justice.game.evaluation.EvaluationContext
import com.drcorchit.justice.lang.environment.MutableEnvironment
import com.drcorchit.justice.lang.environment.MutableTypeEnv
import com.drcorchit.justice.lang.types.source.TypeSource
import org.junit.jupiter.api.Assertions
import kotlin.test.Test

class ExpressionTest {

    @Test
    fun arithmeticTest() {
        val context = EvaluationContext(TypeSource.universe, MutableEnvironment(), false)
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
            val expr = Expression.parse(TypeSource.universe, it.first)
            val actual = expr.evaluate(context).thing
            val expected = it.second
            Assertions.assertEquals(expected, actual, "Error in expression: ${it.first}")
        }
    }

    @Test
    fun lambdaTest() {
        val context = EvaluationContext(TypeSource.universe, MutableEnvironment(), false)
        val dryRunContext = DryRunContext(TypeSource.universe, MutableTypeEnv())
        val actuals = listOf("(() -> 3).invoke()")
        val expecteds = listOf(3)
        actuals.zip(expecteds).forEach {
            val expr = Expression.parse(TypeSource.universe, it.first)
            expr.dryRun(dryRunContext)
            val actual = expr.evaluate(context).thing
            val expected = it.second
            Assertions.assertEquals(expected, actual, "Error in expression: ${it.first}")
        }
    }
}