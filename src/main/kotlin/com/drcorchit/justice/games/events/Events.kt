package com.drcorchit.justice.games.events

import com.drcorchit.justice.games.Game
import com.drcorchit.justice.games.players.Player
import com.drcorchit.justice.utils.json.HttpResult
import com.google.gson.JsonArray
import com.google.gson.JsonObject

interface Events {
    val game: Game

    fun post(player: Player, event: String, info: JsonObject): HttpResult

    fun getEventHistory(since: Long): JsonArray

    val scheduledEvents: ScheduledEvents

}