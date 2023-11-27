package com.drcorchit.justice.lang.types

import com.drcorchit.justice.game.Game
import com.drcorchit.justice.game.mechanics.GameElement
import com.drcorchit.justice.game.mechanics.GameMechanic
import com.drcorchit.justice.utils.logging.Uri
import com.google.gson.JsonElement
import com.google.gson.JsonPrimitive

object ElementType: ReflectionType<GameElement>(GameElement::class) {
    override fun serialize(instance: GameElement): JsonElement {
        return JsonPrimitive(instance.uri.toString())
    }

    override fun deserialize(game: Game, ele: JsonElement): GameElement {
        val uri = Uri.parse(ele.asString)
        return game.mechanics.get<GameMechanic<*>>(uri.parent!!.value)[uri]
    }
}