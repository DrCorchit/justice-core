package com.drcorchit.justice.lang.types

import com.drcorchit.justice.game.Game
import com.drcorchit.justice.game.evaluation.universe.TypeUniverse
import com.drcorchit.justice.game.mechanics.GameMechanic
import com.google.gson.JsonElement
import com.google.gson.JsonPrimitive

object MechanicType : ReflectionType<GameMechanic<*>>(GameMechanic::class, TypeUniverse.getDefault()) {

    override fun serialize(instance: GameMechanic<*>): JsonElement {
        return JsonPrimitive(instance.name)
    }

    override fun deserialize(game: Game, ele: JsonElement): GameMechanic<*> {
        return game.mechanics[ele.asString]
    }
}
