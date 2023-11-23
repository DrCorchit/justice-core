package com.drcorchit.justice.lang.types

import com.drcorchit.justice.game.Game
import com.drcorchit.justice.game.mechanics.GameElement
import com.drcorchit.justice.game.mechanics.GameMechanic
import com.drcorchit.justice.lang.members.Member
import com.drcorchit.justice.lang.members.ReflectionDataMember
import com.drcorchit.justice.utils.json.JsonUtils
import com.google.common.collect.ImmutableMap
import com.google.gson.JsonElement
import com.google.gson.JsonObject
import com.google.gson.JsonPrimitive

//TODO let evaluators have parents.
interface Type<T : Any> : HasType<Type<*>> {

    val clazz: Class<T>

    fun accept(other: Type<*>): Boolean {
        return clazz.isAssignableFrom(other.clazz)
    }

    fun cast(instance: Any): T {
        return clazz.cast(instance)
    }

    fun wrap(instance: Any): TypedThing<T> {
        return TypedThing(cast(instance), this)
    }

    fun getMember(name: String): Member<T>? {
        return members[name]
    }

    val members: ImmutableMap<String, out Member<T>>

    fun serializeCast(instance: Any): JsonElement {
        return serialize(clazz.cast(instance))
    }

    fun serialize(instance: T): JsonElement {
        val output = JsonObject()
        members.values.filterIsInstance<ReflectionDataMember<T>>().forEach {
            val jsonValue = when (val memberValue = it.get(instance)) {
                is GameMechanic<*> -> JsonPrimitive(memberValue.uri.toString())
                is GameElement -> JsonPrimitive(memberValue.uri.toString())
                else -> JsonUtils.GSON.toJsonTree(memberValue)
            }
            output.add(it.name, jsonValue)
        }
        return output
    }

    fun deserialize(game: Game, ele: JsonElement): T {
        //Game Mechanics/Elements are handled via an override
        val instance: T = clazz.getDeclaredConstructor().newInstance()
        members.values.filterIsInstance<ReflectionDataMember<T>>().forEach {
            val json = ele.asJsonObject[it.name]
            it.deserialize(instance, game, json)
        }
        return instance
    }

    override fun getType(): Type<Type<*>> {
        return TypeType
    }
}