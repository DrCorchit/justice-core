package com.drcorchit.justice.lang.environment

import com.drcorchit.justice.lang.types.Type
import com.google.common.collect.ImmutableMap

class ImmutableTypeEnv(
    parameters: Map<String, TypeEnvEntry>,
    override val parent: TypeEnv?,
) : TypeEnv {
    override val map: ImmutableMap<String, TypeEnvEntry> = ImmutableMap.copyOf(parameters)

    override fun declare(id: String, type: Type<*>, mutable: Boolean) {
        throw UnsupportedOperationException("This type environment is immutable!")
    }

    override fun toArgs(): List<Type<*>> {
        return map.values.map { it.type }.toList()
    }
}