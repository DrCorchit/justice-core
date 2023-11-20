package com.drcorchit.justice.game.events

import com.drcorchit.justice.game.players.Player
import com.google.gson.JsonObject

data class EventOutcome(
    val event: String,
    val author: Player,
    val timestamp: Long,
    val latency: Long,
    val info: JsonObject,
    val error: Exception? = null
)