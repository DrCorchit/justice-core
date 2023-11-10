package com.drcorchit.justice.games

import com.drcorchit.utils.json.GSON
import com.drcorchit.utils.normalize
import com.google.gson.JsonElement
import com.google.gson.JsonObject

interface GameElement : Syncable<JsonObject> {
    //The name of the object as seen by the client
    //The name is encouraged to be unique. It may also change from time to time.
    fun name(): String

    //A unique identifier for this element. Not client visible.
    //Should rely on a class with equals and hashcode defined.
    //Contract: The key is unique and does not change under any circumstances.
    //Contract: parent().getElement(key()).equals(this) is always true
    val key: Any
        get() = normalize(name())

    //A short description of the object visible to the client
    fun description(): String

    //Returns the parent game mechanic
    fun parent(): GameMechanic<*>

    //Call this method whenever the element is modified.
    fun touch() {
        parent().touch()
    }

    //Serializes the object so it can be recreated
    fun serialize(): JsonElement {
        return GSON.toJsonTree(this)
    }

    fun serializeForPlayer(player: Player): JsonElement {
        return serialize()
    }
}