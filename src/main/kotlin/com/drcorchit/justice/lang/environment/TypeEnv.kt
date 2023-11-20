package com.drcorchit.justice.lang.environment

import com.drcorchit.justice.game.Game
import com.drcorchit.justice.lang.evaluators.Evaluator
import com.google.gson.JsonObject

interface TypeEnv {

    val parent: TypeEnv?

    val map: Map<String, TypeEnvEntry>

    operator fun get(id: String): Evaluator<*>? {
        return map[id]?.type
    }

    fun declare(id: String, type: Evaluator<*>, mutable: Boolean)

    fun bind(info: JsonObject, game: Game, parent: Environment? = null, mutable: Boolean): Environment {
        val output = MapEnvironment(parent)
        map.entries.forEach {
            val id = it.key
            val type = it.value.type
            val value = type.deserialize(game, info[id])
            output.declare(id, type, mutable)
            output.assign(id, value)
        }
        return output
    }

    fun bind(args: List<Any>): Environment {
        val output = MapEnvironment()
        map.values.forEachIndexed { index, entry ->
            output.declare(entry.id, entry.type, args[index], false)
        }
        return output
    }

    fun toArgs(): List<Evaluator<*>>
}