package com.drcorchit.justice.lang.expression

import com.drcorchit.justice.lang.environment.Environment
import com.drcorchit.justice.lang.environment.TypeEnv
import com.drcorchit.justice.lang.evaluators.Evaluator

interface Expression {

    fun evaluate(env: Environment): Any

    fun dryRun(env: TypeEnv): Evaluator<*>

    companion object {
        @JvmStatic
        fun parse(code: String): Expression {
            TODO()
        }
    }
}