package com.drcorchit.justice.game.events

import com.drcorchit.justice.utils.logging.HasUri
import com.drcorchit.justice.utils.logging.Uri
import com.google.gson.JsonObject
import java.util.concurrent.TimeUnit

interface ScheduledEvent : Runnable, HasUri {
    override val parent: ScheduledEvents
    override val uri: Uri get() = parent.uri.extend(name)
    val name: String
    val delay: Long
    val interval: Long
    val timeUnit: TimeUnit

    fun getLastOccurrence(): ScheduledEventOutcome {
        return getHistory().last()
    }

    fun getNextOccurrence(): Long {
        check(parent.isStarted())
        return if (getHistory().isEmpty()) {
            parent.getLastStarted() + timeUnit.toMillis(delay) + timeUnit.toMillis(interval)
        } else {
            getLastOccurrence().timestamp + timeUnit.toMillis(interval)
        }
    }

    fun getHistory(): List<ScheduledEventOutcome>
    fun serialize(): JsonObject
}