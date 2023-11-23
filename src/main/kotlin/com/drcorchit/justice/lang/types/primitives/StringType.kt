package com.drcorchit.justice.lang.types.primitives

import com.drcorchit.justice.game.Game
import com.drcorchit.justice.lang.types.ReflectionType
import com.drcorchit.justice.lang.types.source.TypeSource
import com.google.gson.JsonElement
import com.google.gson.JsonPrimitive

object StringType : ReflectionType<String>(TypeSource.universe, String::class) {

    override fun serialize(instance: String): JsonElement {
        return JsonPrimitive(instance)
    }

    override fun deserialize(game: Game, ele: JsonElement): String {
        return ele.asString
    }
}