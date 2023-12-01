package com.drcorchit.justice.lang.environment

import com.drcorchit.justice.game.Game
import com.drcorchit.justice.lang.types.Type
import com.google.common.collect.ImmutableList
import com.google.gson.JsonObject

class Parameters(
    val entries: ImmutableList<TypeEnvEntry>,
) {

    fun deserialize(info: JsonObject, game: Game): List<Any> {
        return entries.map {
            val json = info[it.id]
            it.type.deserialize(game, json)
        }
    }

    fun bind(args: List<Any?>): Environment {
        val output = MapEnvironment()
        check(args.size == entries.size) { "Args/Parameter count mismatch." }
        entries.zip(args).forEach {
            output.declare(it.first.id, it.first.type, it.second, it.first.mutable)
        }
        return output
    }

    fun mutableCopy(): MutableTypeEnv {
        val output = MutableTypeEnv()
        entries.forEach { output.declare(it.id, it.type, it.mutable) }
        return output
    }

    companion object {
        @JvmStatic
        val EMPTY = Parameters(ImmutableList.of())

        fun Map<String, Type<*>>.toEnv(): Parameters {
            return Parameters(this.entries.map { TypeEnvEntry(it.key, it.value, false) }
                .let { ImmutableList.copyOf(it) })
        }
    }
}