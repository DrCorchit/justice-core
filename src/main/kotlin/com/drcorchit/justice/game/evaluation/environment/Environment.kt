package com.drcorchit.justice.game.evaluation.environment

import com.drcorchit.justice.lang.types.Type
import com.drcorchit.justice.lang.code.Thing

interface Environment {
    fun lookup(id: String): Thing<*>?

    fun declare(id: String, type: Type<*>, initialValue: Any? = null, mutable: Boolean = false)

    fun assign(id: String, value: Any)
}