package com.drcorchit.justice.lang.environment

import com.drcorchit.justice.game.Game
import com.drcorchit.justice.lang.types.Type
import com.google.common.collect.ImmutableList
import com.google.common.collect.ImmutableMap
import com.google.gson.JsonObject

class ImmutableTypeEnv(
    parameters: Map<String, TypeEnvEntry>,
) {
    private val map: ImmutableMap<String, TypeEnvEntry> = ImmutableMap.copyOf(parameters)

    fun bind(info: JsonObject, game: Game, mutable: Boolean): Environment {
        val output = MapEnvironment()
        map.values.forEach {
            val value = it.type.deserialize(game, info[it.id])
            output.declare(it.id, it.type, value, it.mutable)
        }
        return output
    }

    fun bind(args: List<Any?>): Environment {
        val output = MapEnvironment()
        check(args.size == map.size) { "Args/Parameter count mismatch." }
        map.values.zip(args).forEach {
            output.declare(it.first.id, it.first.type, it.second, it.first.mutable)
        }
        return output
    }


    fun toArgs(): ImmutableList<Type<*>> {
        return map.values.map { it.type }.let { ImmutableList.copyOf(it) }
    }

    fun mutableCopy(): MutableTypeEnv {
        val output = MutableTypeEnv()
        map.values.forEach { output.declare(it.id, it.type, it.mutable) }
        return output
    }
}