package com.drcorchit.justice.game.events

import com.drcorchit.justice.utils.json.JsonUtils.getLong
import com.google.gson.JsonObject
import java.util.concurrent.TimeUnit

class ScheduledEventImpl(
    override val parent: ScheduledEvents,
    override val name: String,
    override val delay: Long,
    override val interval: Long,
    override val timeUnit: TimeUnit,
    private val code: String,
) : ScheduledEvent {
    private val history = mutableListOf<ScheduledEventOutcome>()

    override fun run() {
        val timestamp = System.currentTimeMillis()
        try {
            logger.info("run", "Starting scheduled event")
            val game = parent.parent.parent
            val result = game.execute(game.players.system, code)
            val latency = System.currentTimeMillis() - timestamp
            //val result: JsonResult = events.execute(getContext().with(Players::class.java, Players::getSystemPlayer), statement)
            logger.info("run", "Finished scheduled event result=$result")
            history.add(ScheduledEventOutcome(this, timestamp, latency, null))
        } catch (e: Exception) {
            history.add(ScheduledEventOutcome(this, timestamp, 0, e))
            logger.error("run", "Error in scheduled event $name", e)
        }
    }

    override fun getHistory(): List<ScheduledEventOutcome> {
        return history
    }

    override fun serialize(): JsonObject {
        val output = JsonObject()
        output.addProperty("name", name)
        output.addProperty("delay", delay)
        output.addProperty("interval", interval)
        output.addProperty("timeUnit", timeUnit.name)
        output.addProperty("code", code)
        return output
    }

    companion object {
        fun deserialize(parent: ScheduledEvents, info: JsonObject): ScheduledEvent {
            return ScheduledEventImpl(
                parent,
                info["name"].asString,
                info.getLong("delay", 0),
                info["interval"].asLong,
                TimeUnit.valueOf(info["timeUnit"].asString.uppercase()),
                info["code"].asString
            )
        }
    }

}