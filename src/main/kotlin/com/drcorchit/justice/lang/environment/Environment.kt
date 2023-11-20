package com.drcorchit.justice.lang.environment

import com.drcorchit.justice.lang.evaluators.Evaluator

interface Environment {

    val parent: Environment?

    operator fun get(id: String): Any?

    fun declare(id: String, type: Evaluator<*>, initialValue: Any? = null, mutable: Boolean = false)

    fun assign(id: String, value: Any)

}