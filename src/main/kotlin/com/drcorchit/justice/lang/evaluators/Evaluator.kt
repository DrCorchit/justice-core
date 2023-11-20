package com.drcorchit.justice.lang.evaluators

import com.drcorchit.justice.game.Game
import com.drcorchit.justice.game.mechanics.GameElement
import com.drcorchit.justice.game.mechanics.GameMechanic
import com.drcorchit.justice.lang.members.*
import com.drcorchit.justice.utils.Utils
import com.drcorchit.justice.utils.json.JsonUtils
import com.google.common.cache.LoadingCache
import com.google.common.collect.ImmutableMap
import com.google.gson.JsonElement
import com.google.gson.JsonObject
import com.google.gson.JsonPrimitive
import kotlin.reflect.KClass
import kotlin.reflect.KType
import kotlin.reflect.cast
import kotlin.reflect.full.createInstance
import kotlin.reflect.full.isSubclassOf

interface Evaluator<T : Any> {

    val clazz: KClass<T>

    fun accept(instance: Any): Boolean {
        return clazz.isInstance(instance)
    }

    fun cast(instance: Any): T {
        return clazz.cast(instance)
    }

    fun getMember(name: String): Member<T>? {
        return members[name]
    }

    val members: ImmutableMap<String, Member<T>>

    fun serialize(instance: T): JsonElement {
        val output = JsonObject()
        members.values.filterIsInstance<WrappedDataMember<T>>().forEach {
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
        val instance: T = clazz.createInstance()
        members.values.filterIsInstance<WrappedDataMember<T>>().forEach {
            val json = ele.asJsonObject[it.name]
            it.deserialize(instance, game, json)
        }
        return instance
    }

    companion object {
        @JvmStatic
        fun <T : Any> from(instance: T): Evaluator<T> {
            val clazz: KClass<T> = instance::class as KClass<T>
            return fromClass(clazz)
        }

        fun fromType(type: KType): Evaluator<*> {
            return fromClass(type.classifier as KClass<*>)
        }

        fun <T : Any> fromClass(clazz: KClass<T>): Evaluator<T> {
            return cache.get(clazz) as Evaluator<T>
        }

        private val cache: LoadingCache<KClass<*>, Evaluator<*>> = Utils.createCache(1000) {
            if (it.isSubclassOf(GameMechanic::class)) {
                val temp = it as KClass<GameMechanic<*>>
                MechanicEvaluator(temp)
            } else if (it.isSubclassOf(GameElement::class)) {
                val temp = it as KClass<GameElement>
                ElementEvaluator(temp)
            } else {
                JusticeEvaluator(it)
            }
        }


    }
}