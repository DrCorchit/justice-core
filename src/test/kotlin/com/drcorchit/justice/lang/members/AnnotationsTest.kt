package com.drcorchit.justice.lang.members

import com.drcorchit.justice.lang.annotations.*
import com.drcorchit.justice.lang.evaluators.Evaluator
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

@Mutable
class AnnotationsTest {

    private class Test1 {
        @DataField("Test Mutable Value")
        var w: Int = 3

        @DataField("Test Immutable Value", mutable = true)
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
        val eval = Evaluator.from(test)

        val mutable = eval.getMember("w") as WrappedDataMember
        Assertions.assertEquals(3, mutable.get(test))
        mutable.set(test, 4)
        Assertions.assertEquals(4, mutable.get(test))

        val immutable = eval.getMember("x") as WrappedDataMember
        Assertions.assertEquals(2, immutable.get(test))

        val derived = eval.getMember("y") as WrappedDerivedMember
        Assertions.assertEquals(6, derived.get(test))

        val cached = eval.getMember("z") as WrappedCachedMember
        Assertions.assertEquals(6, cached.get(test))
        test.w = 10
        Assertions.assertEquals(6, cached.get(test))
        cached.invalidate(test)
        Assertions.assertEquals(12, cached.get(test))
    }

    class Test2 {
        @get:DerivedField("Test Derived Field")
        var x = 3

        @JFunction("Foo")
        fun foo(): Int {
            return x
        }

        @JFunction("Bar", true)
        fun bar(): Int {
            return x++
        }
    }

    @Test
    fun functionsTest() {
        //TODO
    }

    fun mechanicTest() {
        //TODO val mech = AbstractMechanic<AbstractMember>()
    }
}