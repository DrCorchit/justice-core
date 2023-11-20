package com.drcorchit.justice.game.events

import com.drcorchit.justice.utils.json.JsonUtils.toJsonArray
import com.drcorchit.justice.utils.json.Result
import com.drcorchit.justice.utils.logging.HasUri
import com.drcorchit.justice.utils.logging.Uri
import com.drcorchit.justice.utils.logging.UriLogger
import com.google.gson.JsonObject

interface ScheduledEvents: HasUri {
    override val parent: Events
    override val uri: Uri get() = parent.uri.extend("scheduled")

    fun getEvent(name: String): ScheduledEvent?
    fun getEvents(): Set<ScheduledEvent>

    fun start(): Result
    fun isStarted(): Boolean
    fun getLastStarted(): Long
    fun stop(): Result

    fun getHistory(): List<ScheduledEventOutcome>
    fun serialize(): JsonObject {
        val output = JsonObject()
        output.addProperty("started", isStarted())
        val events = getEvents().map { it.serialize() }.toJsonArray()
        output.add("events", events)
        return output
    }
}