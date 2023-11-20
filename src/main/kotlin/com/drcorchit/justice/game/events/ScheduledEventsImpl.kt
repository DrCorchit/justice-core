package com.drcorchit.justice.game.events

import com.drcorchit.justice.utils.json.JsonUtils.stream
import com.drcorchit.justice.utils.json.Result
import com.google.gson.JsonObject
import java.util.concurrent.Executors
import java.util.concurrent.ScheduledExecutorService

class ScheduledEventsImpl(override val parent: Events) : ScheduledEvents {
    private val map = mutableMapOf<String, ScheduledEvent>()
    private var lastStarted: Long = 0
    private var service: ScheduledExecutorService? = null

    override fun getEvent(name: String): ScheduledEvent? {
        return map[name]
    }

    override fun getEvents(): Set<ScheduledEvent> {
        return map.values.toSet()
    }

    override fun start(): Result {
        return if (service != null) Result.failWithReason("Events $uri has already started.")
        else try {
            service = Executors.newScheduledThreadPool(map.size)
            for (event in map.values) {
                service!!.scheduleAtFixedRate(event, event.delay, event.interval, event.timeUnit)
            }
            lastStarted = System.currentTimeMillis()
            Result.succeed()
        } catch (e: Exception) {
            //TODO cleanup service if failure
            Result.failWithError(e)
        }
    }

    override fun isStarted(): Boolean {
        return service != null && !service!!.isShutdown
    }

    override fun getLastStarted(): Long {
        return lastStarted
    }

    override fun stop(): Result {
        return if (service == null) {
            Result.failWithReason("ScheduledEvents $uri has already stopped.")
        } else {
            service!!.shutdown()
            service = null
            Result.succeed()
        }
    }

    override fun getHistory(): List<ScheduledEventOutcome> {
        return map.values.flatMap { it.getHistory() }.sorted()
    }

    fun deserialize(info: JsonObject): Result {
        stop()
        map.clear()
        info.getAsJsonArray("events").stream()
            .map { it.asJsonObject }
            .map { ScheduledEventImpl.deserialize(this, it) }
            .forEach { map[it.name] = it }
        return Result.succeed()
    }
}