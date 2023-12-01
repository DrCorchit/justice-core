package com.drcorchit.justice.game.players

import com.drcorchit.justice.game.Game
import com.drcorchit.justice.lang.types.ReflectionType
import com.google.gson.JsonElement
import com.google.gson.JsonPrimitive

object PlayerType : ReflectionType<Player>(Player::class) {
    override fun serialize(instance: Player): JsonElement {
        return JsonPrimitive(instance.id)
    }

    override fun deserialize(game: Game, ele: JsonElement): Player {
        return game.players.getPlayer(ele.asString)!!
    }
}