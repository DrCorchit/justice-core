package com.drcorchit.justice.game.events

import com.drcorchit.justice.game.Game
import com.drcorchit.justice.game.players.Player
import com.drcorchit.justice.lang.evaluators.HasEvaluator
import com.drcorchit.justice.utils.json.HttpResult
import com.drcorchit.justice.utils.logging.HasUri
import com.drcorchit.justice.utils.logging.Uri
import com.google.gson.JsonArray
import com.google.gson.JsonObject

interface Events: HasUri, HasEvaluator<Events> {
    override val parent: Game
    override val uri: Uri get() = parent.uri.extend("events")
    val scheduled: ScheduledEvents

    fun post(player: Player, info: JsonObject): HttpResult
    fun getEventHistory(since: Long): JsonArray

    fun serialize(): JsonObject
    fun deserialize(info: JsonObject)
}