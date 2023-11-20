package com.drcorchit.justice.lang.environment

import com.drcorchit.justice.lang.evaluators.Evaluator

class MutableTypeEnv(override val parent: TypeEnv? = null) : TypeEnv {
    override val map = mutableMapOf<String, TypeEnvEntry>()

    override fun get(id: String): Evaluator<*>? {
        return map[id]?.type
    }

    override fun declare(id: String, type: Evaluator<*>, mutable: Boolean) {
        check(map[id] == null)
        map[id] = TypeEnvEntry(id, type, mutable)
    }

    override fun toArgs(): List<Evaluator<*>> {
        return map.values.map { it.type }
    }

}