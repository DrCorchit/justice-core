package com.drcorchit.justice.game.players

import com.drcorchit.justice.game.Game
import com.drcorchit.justice.lang.annotations.DerivedField
import com.drcorchit.justice.lang.annotations.JFunction
import com.drcorchit.justice.lang.types.Thing
import com.drcorchit.justice.utils.json.Result
import com.drcorchit.justice.utils.logging.HasUri
import com.drcorchit.justice.utils.logging.Uri
import com.google.gson.JsonObject

interface Players : Set<Player>, HasUri {
    override val parent: Game
    override val uri: Uri get() = parent.uri.extend("players")
    val asThing: Thing<Players> get() = Thing(this, PlayersType)

    @get:DerivedField("The minimum number of players required to play the game.")
    val minPlayerCount: Int

    @get:DerivedField("The maximum number of players allowed to play concurrently.")
    val maxPlayerCount: Int

    @JFunction("Returns the player with the given id or username.", false)
    fun getPlayer(usernameOrID: String): Player?

    @JFunction("Adds the given player to the game.", true)
    fun addPlayer(player: Player): Result

    @JFunction("Removes the given player from the game.", true)
    fun removePlayer(player: Player): Result

    fun serialize(): JsonObject
    fun deserialize(info: JsonObject)

    @get:DerivedField("Returns the number of players currently in the game.")
    override val size: Int
}