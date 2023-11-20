package com.drcorchit.justice.game.mechanics

import com.drcorchit.justice.game.Game
import com.google.gson.JsonElement
import com.google.gson.JsonPrimitive

class MechRef<T : GameMechanic<*>>(val game: Game, val mechID: MechID<T>) {
    val value: T get() = game.mechanics[mechID.name]

    fun serialize(): JsonElement {
        return JsonPrimitive(toString())
    }

    override fun toString(): String {
        return "${game.id}.${mechID.name}"
    }
}

class Ref<T : GameElement>(val ref: MechRef<GameMechanic<T>>, val id: Any) {
    val value: T get() = ref.value[id]

    fun serialize(): JsonElement {
        return JsonPrimitive(toString())
    }

    override fun toString(): String {
        return "$ref.$id"
    }
}