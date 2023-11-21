package com.drcorchit.justice.lang.evaluators

import com.drcorchit.justice.game.Game
import com.google.gson.JsonElement

abstract class NonSerializableEvaluator<T : Any> : Evaluator<T> {
    override fun serialize(instance: T): JsonElement {
        throw UnsupportedOperationException("Class ${clazz.qualifiedName} is not serializable.")
    }

    override fun deserialize(game: Game, ele: JsonElement): T {
        throw UnsupportedOperationException("Class ${clazz.qualifiedName} is not deserializable.")
    }
}