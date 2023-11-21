package com.drcorchit.justice.game.players

import com.drcorchit.justice.game.Game
import com.drcorchit.justice.lang.evaluators.HasEvaluator
import com.drcorchit.justice.utils.json.Result
import com.drcorchit.justice.utils.logging.HasUri
import com.drcorchit.justice.utils.logging.Uri
import com.drcorchit.justice.utils.logging.UriLogger
import com.google.gson.JsonObject

interface Players: Set<Player>, HasUri, HasEvaluator<Players> {
    override val parent: Game
    override val uri: Uri get() = parent.uri.extend("players")

    //This player object is used when generating internal events and is not actually part of the roster.
    val system: Player
    val minPlayerCount: Int
    val maxPlayerCount: Int

    fun getPlayer(usernameOrID: String): Player?
    fun addPlayer(player: Player): Result
    fun removePlayer(player: Player): Result

    fun serialize(): JsonObject
    fun deserialize(info: JsonObject)
}