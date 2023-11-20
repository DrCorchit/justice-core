package com.drcorchit.justice.lang.evaluators

import com.drcorchit.justice.game.Game
import com.google.gson.JsonElement
import com.google.gson.JsonPrimitive

object StringEvaluator : JusticeEvaluator<String>(String::class) {

    override fun serialize(instance: String): JsonElement {
        return JsonPrimitive(instance)
    }

    override fun deserialize(game: Game, ele: JsonElement): String {
        return ele.asString
    }
}