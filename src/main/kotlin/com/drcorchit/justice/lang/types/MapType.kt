package com.drcorchit.justice.lang.types

import com.drcorchit.justice.exceptions.JusticeException
import com.drcorchit.justice.exceptions.SerializationException
import com.drcorchit.justice.game.Game
import com.drcorchit.justice.game.evaluation.environment.Parameters.Companion.toEnv
import com.drcorchit.justice.game.mechanics.GameElement
import com.drcorchit.justice.game.mechanics.GameMechanic
import com.drcorchit.justice.game.players.Player
import com.drcorchit.justice.game.players.PlayerType
import com.drcorchit.justice.lang.members.lambda.LambdaMember
import com.drcorchit.justice.lang.types.primitives.*
import com.google.gson.JsonElement
import com.google.gson.JsonObject
import com.google.gson.JsonPrimitive
import kotlin.reflect.full.isSuperclassOf

class MapType(val keyType: Type<*>, val valueType: Type<*>) : Type<Map<*, *>>(Map::class) {

    init {
        val isNumber = Number::class.isSuperclassOf(keyType.clazz)
        val isString = keyType is StringType
        val isEnum = keyType is EnumType
        val isMech = keyType is GameMechanicType
        val isElement = keyType is ElementType
        val isPlayer = keyType is PlayerType
        if (!(isNumber || isString || isEnum || isMech || isElement || isPlayer)) {
            throw JusticeException("Invalid map key: $keyType. Map key must be a string, number, enum, mechanic, element, or player.")
        }
    }

    override val members = listOf(
        LambdaMember(
            this,
            "get",
            "Retrieves the value from the map.",
            mapOf("key" to keyType).toEnv(),
            valueType,
            false
        ) { instance, args -> instance[args[0]] ?: Unit }
    ).toMemberMap()

    override fun serialize(instance: Map<*, *>): JsonElement {
        val output = JsonObject()
        instance.entries.forEach {
            val key = serializeKey(it.key!!)
            val value = valueType.wrap(it.value!!).serialize()
            output.add(key, value)
        }
        return output
    }

    override fun deserialize(game: Game, ele: JsonElement): Map<*, *> {
        val output = LinkedHashMap<Any, Any>()
        ele.asJsonObject.entrySet().forEach {
            val key = deserializeKey(game, it.key)
            val value = valueType.deserialize(game, it.value)
            output[key] = value
        }
        return output
    }

    private fun serializeKey(key: Any): String {
        return when (key) {
            is Number, String -> key.toString()
            is Enum<*> -> key.name
            is GameMechanic<*> -> key.name
            is GameElement -> "${key.parent.name}.${key.id}"
            is Player -> key.id
            else -> throw SerializationException("Unable to serialize map key to string.")
        }
    }

    private fun deserializeKey(game: Game, key: String): Any {
        return when (keyType) {
            is IntType -> key.toInt()
            is LongType -> key.toLong()
            is NumberType, RealType -> key.toDouble()
            is StringType -> key
            else -> keyType.deserialize(game, JsonPrimitive(key))
        }
    }
}