package com.drcorchit.justice.lang.types

import com.drcorchit.justice.exceptions.DeserializationException
import com.drcorchit.justice.game.Game
import com.drcorchit.justice.game.mechanics.GameElement
import com.drcorchit.justice.game.mechanics.GameMechanic
import com.google.gson.JsonElement
import com.google.gson.JsonPrimitive

object ElementType : ReflectionType<GameElement>(GameElement::class) {
    override fun serialize(instance: GameElement): JsonElement {
        return JsonPrimitive("${instance.parent.name}.${instance.id}")
    }

    //We allow commas for the sake of GridElements
    private val elementRegex = "([a-zA-Z0-9_])\\.([a-zA-Z0-9,_])".toRegex()

    override fun deserialize(game: Game, ele: JsonElement): GameElement {
        val match = elementRegex.matchEntire(ele.asString)?.groupValues
            ?: throw DeserializationException("Cannot deserialize element: ID is not parsable.")
        val parent = match[0]
        val id = match[1]
        return game.mechanics.get<GameMechanic<*>>(parent)[id]
    }
}