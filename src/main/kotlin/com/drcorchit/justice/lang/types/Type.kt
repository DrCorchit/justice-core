package com.drcorchit.justice.lang.types

import com.drcorchit.justice.exceptions.TypeException
import com.drcorchit.justice.game.Game
import com.drcorchit.justice.lang.code.Thing
import com.drcorchit.justice.lang.members.DataFieldMember
import com.drcorchit.justice.lang.members.Member
import com.google.common.collect.ImmutableMap
import com.google.gson.JsonElement
import com.google.gson.JsonObject

interface Type<T : Any> {
    val parent: Type<in T>? get() = AnyType
    val clazz: Class<T>

    fun accept(other: Type<*>): Boolean {
        return clazz.isAssignableFrom(other.clazz)
    }

    fun cast(instance: Any): T {
        try {
            return clazz.cast(instance)
        } catch (e: Exception) {
            throw TypeException("cast", clazz.name, instance::class.qualifiedName ?: "<anonymous>")
        }
    }

    fun wrap(instance: Any): Thing<T> {
        return Thing(cast(instance), this)
    }

    val members: ImmutableMap<String, Member<T>>
    //val inheritedMembers: ImmutableMap<String>

    fun getMember(name: String): Member<T>? {
        return members[name] ?: parent?.getMember(name)
    }

    fun serialize(instance: T): JsonElement {
        val output = JsonObject()
        //TODO serialize parent members
        members.values.filterIsInstance<DataFieldMember<T>>().forEach {
            val jsonValue = it.getAndWrap(instance).serialize()
            output.add(it.name, jsonValue)
        }
        return output
    }

    fun deserialize(game: Game, ele: JsonElement): T {
        //Game Mechanics/Elements are handled via an override
        val instance: T = clazz.getDeclaredConstructor().newInstance()
        sync(instance, game, ele.asJsonObject)
        return instance
    }

    fun sync(instance: Any, game: Game, info: JsonObject) {
        val thing = cast(instance)
        members.values.filterIsInstance<DataFieldMember<T>>().forEach {
            it.deserialize(thing, game, info[it.name])
        }
    }

    companion object {
        fun <T : Any> List<Member<T>>.toMemberMap(): ImmutableMap<String, Member<T>> {
            return this.associateBy { it.name }.let { ImmutableMap.copyOf(it) }
        }
    }
}