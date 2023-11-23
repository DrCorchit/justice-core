package com.drcorchit.justice.lang.expression

import com.drcorchit.justice.game.evaluation.EvaluationContext
import com.drcorchit.justice.lang.environment.MutableEnvironment
import com.drcorchit.justice.lang.types.source.TypeSource
import org.junit.jupiter.api.Assertions
import kotlin.test.Test

class ExpressionTest {

    @Test
    fun constantTest() {
        val temp = MutableEnvironment()
        val actual = Expression.parse(TypeSource.universe, "2+2")
        Assertions.assertEquals(4, actual.evaluate(EvaluationContext()))
    }
}