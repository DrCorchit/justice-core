package com.drcorchit.justice.game.events

import com.drcorchit.justice.game.Game
import com.drcorchit.justice.game.players.Player
import com.drcorchit.justice.lang.types.Type
import com.drcorchit.justice.lang.code.Thing
import com.drcorchit.justice.utils.json.HttpResult
import com.drcorchit.justice.utils.logging.HasUri
import com.drcorchit.justice.utils.logging.Uri
import com.google.gson.JsonArray
import com.google.gson.JsonObject

interface Events: HasUri {
    override val parent: Game
    override val uri: Uri get() = parent.uri.extend("events")
    val scheduled: ScheduledEvents
    val asThing: Thing<Events> get() = Thing(this, getType())

    fun post(player: Player, info: JsonObject): HttpResult
    fun getEventHistory(since: Long): JsonArray

    fun getType(): Type<Events>


    fun serialize(): JsonObject
    fun deserialize(info: JsonObject)
}