package com.drcorchit.justice.lang.environment

import com.drcorchit.justice.lang.types.Thing
import com.drcorchit.justice.lang.types.Type

class MapEnvironment : Environment {
    val map = mutableMapOf<String, EnvEntry>()

    override fun lookup(id: String): Thing<*>? {
        val entry = map[id]
        return entry?.type?.wrap(entry.value!!)
    }

    override fun declare(id: String, type: Type<*>, initialValue: Any?, mutable: Boolean) {
        check(map[id] == null) { "Variable redeclaration detected: $id" }
        //check(initialValue != null || mutable) { "Permanently immutable variable detected." }
        map[id] = EnvEntry(id, initialValue, type, mutable)
    }

    override fun assign(id: String, value: Any) {
        map[id]!!.value = value
    }

    data class EnvEntry(val id: String, var value: Any?, val type: Type<*>, val mutable: Boolean)
}