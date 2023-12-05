package com.drcorchit.justice.game.evaluation.environment

import com.drcorchit.justice.lang.types.Type

interface TypeEnv {
    fun lookup(id: String): Type<*>?

    fun declare(id: String, type: Type<*>, mutable: Boolean = false)

    fun assign(id: String, value: Type<*>)
}