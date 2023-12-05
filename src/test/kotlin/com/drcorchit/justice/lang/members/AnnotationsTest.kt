package com.drcorchit.justice.lang.members

import com.drcorchit.justice.game.evaluation.universe.TypeUniverse
import com.drcorchit.justice.lang.annotations.CachedField
import com.drcorchit.justice.lang.annotations.DataField
import com.drcorchit.justice.lang.annotations.DerivedField
import com.drcorchit.justice.lang.annotations.Evaluable
import com.drcorchit.justice.lang.members.reflection.CachedReflectionMember
import com.drcorchit.justice.lang.members.reflection.DerivedReflectionMember
import com.drcorchit.justice.lang.members.reflection.ReflectionDataMember
import com.drcorchit.justice.lang.types.ReflectionType
import com.drcorchit.justice.utils.json.JsonUtils
import com.google.gson.JsonParser
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

class AnnotationsTest {

    class Test1 {
        @DataField("Test Mutable Value", mutable = true)
        var w: Int = 3

        @DataField("Test Immutable Value", mutable = false)
        var x: Int = 2

        @DerivedField("Test Derived Value")
        fun y(): Int {
            return w + x
        }

        @CachedField("Test Cached Value", 10)
        fun z(): Int {
            return w + x
        }
    }

    @Test
    fun fieldsTest() {
        val test = Test1()
        val eval = ReflectionType(Test1::class)

        val mutable = eval.getMember("w") as ReflectionDataMember
        Assertions.assertEquals(3, mutable.get(test))
        mutable.set(test, 4)
        Assertions.assertEquals(4, mutable.get(test))

        val immutable = eval.getMember("x") as ReflectionDataMember
        Assertions.assertEquals(2, immutable.get(test))

        val derived = eval.getMember("y") as DerivedReflectionMember
        Assertions.assertEquals(6, derived.get(test))

        val cached = eval.getMember("z") as CachedReflectionMember
        Assertions.assertEquals(6, cached.get(test))
        test.w = 10
        Assertions.assertEquals(6, cached.get(test))
        cached.invalidate(test)
        Assertions.assertEquals(12, cached.get(test))
    }

    class Test2 {
        @get:DerivedField("Test Derived Field")
        var x = 3

        @Evaluable("Foo")
        fun foo(y: Int, z: Int): Int {
            return x + y + z
        }

        @Evaluable("Bar", true)
        fun bar(): Int {
            return x++
        }
    }

    @Test
    fun functionsTest() {
        val test = Test2()
        val eval = ReflectionType(Test2::class)
        val foo = eval.getMember("foo")!!
        val bar = eval.getMember("bar")!!
        Assertions.assertEquals(10, foo.apply(test, listOf(3, 4)))
        Assertions.assertEquals(3, bar.apply(test, listOf()))
        Assertions.assertEquals(6, foo.apply(test, listOf(1, 1)))
    }

    class Test3 {
        @DataField
        var array = arrayOf(1, 2, 3, 4)

        @DataField
        var list = listOf("a", "b", "c")

        @DataField
        var map = mapOf<String, Int>("a" to 1, "b" to 2)
    }

    @Test
    fun genericsTest() {
        val universe = TypeUniverse.getDefault()
        val type = ReflectionType(Test3::class, universe)

        val actual = type.serialize(Test3())
        val expected = JsonUtils::class.java.getResourceAsStream("expected3.json")!!
            .readAllBytes().let { JsonParser.parseString(String(it)) }.asJsonObject

        Assertions.assertEquals(actual, expected)
    }
}