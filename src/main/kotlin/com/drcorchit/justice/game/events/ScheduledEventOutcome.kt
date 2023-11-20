package com.drcorchit.justice.game.events

import com.drcorchit.justice.utils.json.JsonUtils
import com.google.gson.JsonObject

data class ScheduledEventOutcome(val event: ScheduledEvent, val timestamp: Long, val latency: Long, val error: Exception?) :
    Comparable<ScheduledEventOutcome> {
    fun serialize(): JsonObject {
        return JsonUtils.GSON.toJsonTree(this).asJsonObject
    }

    companion object {
        fun deserialize(info: JsonObject): ScheduledEventOutcome {
            return JsonUtils.GSON.fromJson(info, ScheduledEventOutcome::class.java)
        }
    }

    override fun compareTo(other: ScheduledEventOutcome): Int {
        return timestamp.compareTo(other.timestamp)
    }
}