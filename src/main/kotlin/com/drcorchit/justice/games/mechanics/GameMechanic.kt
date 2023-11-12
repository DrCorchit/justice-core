package com.drcorchit.justice.games.mechanics

import com.drcorchit.justice.games.Game
import com.drcorchit.justice.games.players.Player
import com.drcorchit.justice.utils.json.JsonUtils.Companion.GSON
import com.google.gson.JsonObject

interface GameMechanic<T : GameElement> : Syncable<JsonObject>, Iterable<T> {
    val name: String get() = javaClass.simpleName

    val parent: Game

    var lastModified: Long

    fun size(): Int

    fun has(key: Any): Boolean

    operator fun get(key: Any): T

    //Some game elements have a default member. (E.g. Buffs, Resources, Features). This function is optional.
    //Game elements which do not have a default member should throw a NoSuchElementException when this is called
    val defaultElement: T?

    //Call this method whenever the mechanic is modified. This should also update lastModified
    fun touch()

    fun serialize(): JsonObject {
        return GSON.toJsonTree(this).asJsonObject
    }

    fun serializeForPlayer(player: Player): JsonObject {
        return serialize()
    }
}