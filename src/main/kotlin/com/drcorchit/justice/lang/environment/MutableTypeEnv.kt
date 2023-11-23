package com.drcorchit.justice.lang.environment

import com.drcorchit.justice.lang.types.Type

class MutableTypeEnv(override val parent: TypeEnv? = null) : TypeEnv {
    override val map = mutableMapOf<String, TypeEnvEntry>()

    override fun get(id: String): Type<*>? {
        return map[id]?.type
    }

    override fun declare(id: String, type: Type<*>, mutable: Boolean) {
        check(map[id] == null)
        map[id] = TypeEnvEntry(id, type, mutable)
    }

    override fun toArgs(): List<Type<*>> {
        return map.values.map { it.type }
    }

    fun immutableCopy(): ImmutableTypeEnv {
        return ImmutableTypeEnv(map, parent)
    }

}