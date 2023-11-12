package com.drcorchit.justice.games.events

import com.drcorchit.justice.games.Game
import com.drcorchit.justice.utils.json.Result
import com.drcorchit.justice.utils.json.toJsonArray
import com.google.common.collect.ImmutableMap
import com.google.gson.JsonObject

interface ScheduledEvents {
    val game: Game
    val map: ImmutableMap<String, ScheduledEvent>
    fun start(): Result
    fun isStarted(): Boolean
    fun stop(): Result
    fun getHistory(): JsonObject
    fun serialize(): JsonObject {
        val output = JsonObject()
        output.addProperty("started", isStarted())
        val events = map.values.map { it.serialize() }.toJsonArray()
        output.add("events", events)
        return output
    }
}