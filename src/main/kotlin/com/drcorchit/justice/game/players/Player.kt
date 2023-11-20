package com.drcorchit.justice.game.players

import com.google.gson.JsonObject

interface Player {
    //Uniquely identifies the player and will never change.
    val id: String

    var name: String

    var moderator: Boolean

    var human: Boolean

    fun serialize(): JsonObject
}