package com.drcorchit.justice.lang.environment

import com.drcorchit.justice.lang.types.Type

data class TypeEnvEntry(val id: String, val type: Type<*>, val mutable: Boolean)