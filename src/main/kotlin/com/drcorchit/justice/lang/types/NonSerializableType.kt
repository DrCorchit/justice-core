package com.drcorchit.justice.lang.types

import com.drcorchit.justice.exceptions.DeserializationException
import com.drcorchit.justice.exceptions.SerializationException
import com.drcorchit.justice.game.Game
import com.google.gson.JsonElement
import kotlin.reflect.KClass

abstract class NonSerializableType<T : Any>(clazz: KClass<T>, parent: Type<in T>? = AnyType, ) : Type<T>(clazz, parent) {
    override fun serialize(instance: T): JsonElement {
        throw SerializationException("Class $clazz is not serializable.")
    }

    override fun deserialize(game: Game, ele: JsonElement): T {
        throw DeserializationException("Class $clazz is not deserializable.")
    }
}