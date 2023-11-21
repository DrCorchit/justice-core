package com.drcorchit.justice.lang.environment

import com.drcorchit.justice.lang.evaluators.Evaluator
import com.google.common.collect.ImmutableMap

class ImmutableTypeEnv(
    parameters: Map<String, TypeEnvEntry>,
    override val parent: TypeEnv?,
) : TypeEnv {
    override val map: ImmutableMap<String, TypeEnvEntry> = ImmutableMap.copyOf(parameters)

    override fun declare(id: String, type: Evaluator<*>, mutable: Boolean) {
        throw UnsupportedOperationException("This type environment is immutable!")
    }

    override fun toArgs(): List<Evaluator<*>> {
        return map.values.map { it.type }.toList()
    }
}