package com.drcorchit.justice.lang.environment

import com.drcorchit.justice.exceptions.IllegalAssignmentException
import com.drcorchit.justice.exceptions.TypeException
import com.drcorchit.justice.lang.types.Type

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

    fun immutableCopy(): ImmutableTypeEnv {
        return ImmutableTypeEnv(map)
    }
}