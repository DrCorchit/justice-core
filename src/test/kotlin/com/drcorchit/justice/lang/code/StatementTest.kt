package com.drcorchit.justice.lang.code

import com.drcorchit.justice.exceptions.JusticeRuntimeException
import com.drcorchit.justice.game.evaluation.context.StackDryRunContext
import com.drcorchit.justice.game.evaluation.context.StackExecutionContext
import com.drcorchit.justice.game.evaluation.universe.TypeUniverse
import com.drcorchit.justice.lang.code.statement.Statement
import com.drcorchit.justice.lang.types.Type
import com.drcorchit.justice.lang.types.primitives.IntType
import com.drcorchit.justice.lang.types.primitives.StringType
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

class StatementTest {

    @Test
    fun assignTest() {
        listOf(
            TestCase("val x = 5; var y = 3; y = x * y; y;", IntType, 15),
            TestCase("val s = \"test\"; val t = \"ing\"; val r = s + t; r;", StringType, "testing")
        ).forEach { it.test() }
    }

    @Test
    fun errorTest() {
        val universe = TypeUniverse.getDefault()
        val context = StackExecutionContext(universe, true)
        val drc = StackDryRunContext(universe, true)

        val codes = listOf(
            "throw \"Test error\";",
            "1==2 throws \"No error\";",
            "2==2 throws \"Error\";"
        ).map {
            val stmt = Statement.parse(universe, it)
            stmt.dryRun(drc)
            stmt
        }

        Assertions.assertThrows(JusticeRuntimeException::class.java) { codes[0].run(context) }
        Assertions.assertEquals(Thing.UNIT, codes[1].run(context))
        Assertions.assertThrows(JusticeRuntimeException::class.java) { codes[2].run(context) }
    }

    @Test
    fun forTest() {
        listOf(
            TestCase("var x = 0; for (y: 0...4) {x = x+y;} x;", IntType, 10)
        ).forEach { it.test() }
    }

    @Test
    fun ifTest() {
        listOf(
            TestCase("var x = 3; val y = 6; if (x > y) { x = y; } x;", IntType, 3),
            TestCase("var x = 9; val y = 6; if (x > y) { x = y; } x;", IntType, 6),
            TestCase("var x = 3; val y = 6; if (x > y) { x = y; } else { x = 100; } x;", IntType, 100),
            TestCase("var x = 9; val y = 6; if (x > y) { x = y; } else { x = 100; } x;", IntType, 6)
        ).forEach { it.test() }
    }

    @Test
    fun whileTest() {
        listOf(
            TestCase("var x = 0; while (x < 10) { x = x + 2; } x;", IntType, 10)
        )
    }

    private class TestCase(val code: String, val expectedType: Type<*>, val expectedValue: Any) {
        fun test() {
            val universe = TypeUniverse.getDefault()
            val context = StackExecutionContext(universe, true)
            val drc = StackDryRunContext(universe, true)

            val stmt = Statement.parse(universe, code)
            Assertions.assertEquals(expectedType, stmt.dryRun(drc))
            Assertions.assertEquals(expectedValue, stmt.run(context).value)
        }
    }
}