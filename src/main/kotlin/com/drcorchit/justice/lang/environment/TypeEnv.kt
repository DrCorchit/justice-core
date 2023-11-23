package com.drcorchit.justice.lang.environment

import com.drcorchit.justice.exceptions.MemberNotFoundException
import com.drcorchit.justice.exceptions.TypeException
import com.drcorchit.justice.game.Game
import com.drcorchit.justice.lang.types.Type
import com.google.gson.JsonObject

interface TypeEnv {

    val parent: TypeEnv?

    val map: Map<String, TypeEnvEntry>

    operator fun get(id: String): Type<*>? {
        return map[id]?.type
    }

    fun declare(id: String, type: Type<*>, mutable: Boolean)

    fun assignDry(id: String, type: Type<*>) {
        //TODO immutable assignment check
        val expectedType = get(id)
        if (expectedType == null) {
            throw MemberNotFoundException("env", id)
        } else {
            if (!expectedType.accept(type)) {
                throw TypeException("env", expectedType, type)
            }
        }
    }

    fun bind(info: JsonObject, game: Game, parent: Environment? = null, mutable: Boolean): Environment {
        val output = MutableEnvironment(parent)
        map.entries.forEach {
            val id = it.key
            val type = it.value.type
            val value = type.deserialize(game, info[id])
            output.declare(id, type, mutable)
            output.assign(id, value)
        }
        return output
    }

    fun bind(parent: Environment?, args: List<Any?>): Environment {
        val output = MutableEnvironment(parent)
        map.values.forEachIndexed { index, entry ->
            output.declare(entry.id, entry.type, args[index], false)
        }
        return output
    }

    fun toArgs(): List<Type<*>>
}