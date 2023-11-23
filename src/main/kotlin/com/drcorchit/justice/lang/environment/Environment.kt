package com.drcorchit.justice.lang.environment

import com.drcorchit.justice.lang.types.Type
import com.drcorchit.justice.lang.types.TypedThing

interface Environment {

    val parent: Environment?

    operator fun get(id: String): TypedThing<*>?

    fun declare(id: String, type: Type<*>, initialValue: Any? = null, mutable: Boolean = false)

    fun assign(id: String, value: Any)

    fun toTypeEnv(): TypeEnv
}