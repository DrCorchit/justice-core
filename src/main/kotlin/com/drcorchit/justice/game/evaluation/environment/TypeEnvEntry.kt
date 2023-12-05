package com.drcorchit.justice.game.evaluation.environment

import com.drcorchit.justice.lang.types.Type

data class TypeEnvEntry(val id: String, val type: Type<*>, val mutable: Boolean)