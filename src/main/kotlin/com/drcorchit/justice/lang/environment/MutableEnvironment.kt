package com.drcorchit.justice.lang.environment

import com.drcorchit.justice.lang.evaluators.Evaluator

class MutableEnvironment(override val parent: Environment? = null) : Environment {
    val map = mutableMapOf<String, EnvEntry>()

    override fun get(id: String): Any? {
        return map[id]?.value
    }

    override fun declare(id: String, type: Evaluator<*>, initialValue: Any?, mutable: Boolean) {
        check(map[id] == null)
        map[id] = EnvEntry(id, initialValue, type, mutable)
    }

    override fun assign(id: String, value: Any) {
        map[id]!!.value = value
    }

    override fun toTypeEnv(): TypeEnv {
        val output = MutableTypeEnv()
        map.values.forEach { output.declare(it.id, it.type, it.mutable) }
        return output
    }

    data class EnvEntry(val id: String, var value: Any?, val type: Evaluator<*>, val mutable: Boolean)
}