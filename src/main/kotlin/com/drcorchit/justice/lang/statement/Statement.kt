package com.drcorchit.justice.lang.statement

import com.drcorchit.justice.lang.environment.Environment
import com.drcorchit.justice.lang.environment.TypeEnv
import com.drcorchit.justice.lang.evaluators.Evaluator
import com.drcorchit.justice.lang.expression.Expression
import kotlin.jvm.Throws

interface Statement {

    //A statement MAY yield a value when run.
    @Throws(ReturnException::class)
    fun execute(env: Environment): Any?

    @Throws(ReturnTypeException::class)
    fun dryRun(env: TypeEnv): Evaluator<*>?

    companion object {
        @JvmStatic
        fun parse(code: String): Statement {
            TODO()
        }
    }
}