package com.drcorchit.justice.game.evaluation.environment

import com.drcorchit.justice.exceptions.IllegalAssignmentException
import com.drcorchit.justice.exceptions.TypeException
import com.drcorchit.justice.lang.types.Type
import com.google.common.collect.ImmutableList

class MutableTypeEnv : TypeEnv {
    private val map = mutableMapOf<String, TypeEnvEntry>()

    override fun lookup(id: String): Type<*>? {
        return map[id]?.type
    }

    override fun declare(id: String, type: Type<*>, mutable: Boolean) {
        check(map[id] == null)
        map[id] = TypeEnvEntry(id, type, mutable)
    }

    override fun assign(id: String, value: Type<*>) {
        val entry = map[id]
        if (entry == null) {
            throw IllegalAssignmentException("Cannot assign a value before it has been declared.")
        } else if (!entry.mutable) {
            throw IllegalAssignmentException("Cannot assign an immutable value.")
        } else if (!entry.type.accept(value)) {
            throw TypeException(id, entry.type, value)
        }
    }

    fun immutableCopy(): Parameters {
        return Parameters(ImmutableList.copyOf(map.values))
    }

    override fun toString(): String {
        val values = map.values.joinToString(", ") {
            val declarator = if (it.mutable) "var" else "val"
            "$declarator ${it.id}: ${it.type}"
        }
        return "($values)"
    }
}