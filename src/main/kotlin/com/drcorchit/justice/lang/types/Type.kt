package com.drcorchit.justice.lang.types

import com.drcorchit.justice.exceptions.TypeException
import com.drcorchit.justice.game.Game
import com.drcorchit.justice.lang.code.Thing
import com.drcorchit.justice.lang.members.DataFieldMember
import com.drcorchit.justice.lang.members.Member
import com.google.common.collect.ImmutableMap
import com.google.common.collect.ImmutableSet
import com.google.gson.JsonElement
import com.google.gson.JsonObject
import kotlin.reflect.KClass
import kotlin.reflect.full.cast
import kotlin.reflect.full.createInstance
import kotlin.reflect.full.isSuperclassOf

//Parent is null only for AnyType and UnitType.
abstract class Type<T : Any>(val clazz: KClass<T>, val parent: Type<in T>? = AnyType) {

    open fun accept(other: Type<*>): Boolean {
        return clazz.isSuperclassOf(other.clazz)
    }

    open fun cast(instance: Any): T {
        try {
            return clazz.cast(instance)
        } catch (e: Exception) {
            throw TypeException("cast", clazz, instance::class)
        }
    }

    fun wrap(instance: Any): Thing<T> {
        return Thing(cast(instance), this)
    }

    abstract val members: ImmutableMap<String, Member<T>>

    private val allMemberNames: ImmutableSet<String> by lazy {
        val temp = members.keys + (parent?.members?.keys ?: ImmutableSet.of())
        ImmutableSet.copyOf(temp)
    }

    open val allMembers: ImmutableMap<String, Member<T>> by lazy {
        allMemberNames.map { getMember(it)!! }.associateBy { it.name }.let { ImmutableMap.copyOf(it) }
    }

    open fun getMember(name: String): Member<T>? {
        return members[name] ?: parent?.getMember(name)
    }

    open fun serialize(instance: T): JsonElement {
        return serializeByReflection(instance)
    }

    open fun serializeByReflection(instance: Any): JsonElement {
        val thing = cast(instance)
        val output = JsonObject()
        allMembers.values.filterIsInstance<DataFieldMember<T>>().forEach {
            val jsonValue = it.getAndWrap(thing).serialize()
            output.add(it.name, jsonValue)
        }
        return output
    }

    open fun deserialize(game: Game, ele: JsonElement): T {
        val instance = clazz.createInstance()
        sync(instance, game, ele.asJsonObject)
        return instance
    }

    open fun sync(instance: Any, game: Game, info: JsonObject) {
        val thing = cast(instance)
        members.values.filterIsInstance<DataFieldMember<T>>().forEach {
            it.deserialize(thing, game, info.get(it.name))
        }
    }

    override fun toString(): String {
        return "Type<${clazz.qualifiedName}>"
    }

    companion object {
        fun <T : Any> List<Member<T>>.toMemberMap(): ImmutableMap<String, Member<T>> {
            return this.associateBy { it.name }.let { ImmutableMap.copyOf(it) }
        }
    }
}