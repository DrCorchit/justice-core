package com.drcorchit.justice.lang.environment

import com.drcorchit.justice.lang.evaluators.Evaluator

data class TypeEnvEntry(val id: String, val type: Evaluator<*>, val mutable: Boolean)