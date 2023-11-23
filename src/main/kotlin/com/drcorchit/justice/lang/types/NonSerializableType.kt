package com.drcorchit.justice.lang.types

import com.drcorchit.justice.game.Game
import com.google.gson.JsonElement

abstract class NonSerializableType<T : Any> : Type<T> {
    override fun serialize(instance: T): JsonElement {
        throw UnsupportedOperationException("Class ${clazz.name} is not serializable.")
    }

    override fun deserialize(game: Game, ele: JsonElement): T {
        throw UnsupportedOperationException("Class ${clazz.name} is not deserializable.")
    }
}