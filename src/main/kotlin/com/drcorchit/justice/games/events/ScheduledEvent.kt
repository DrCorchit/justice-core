package com.drcorchit.justice.games.events

import com.google.gson.JsonObject

interface ScheduledEvent {
    val name: String
    fun run()
    fun getLastOccurance(): ScheduledEventOutcome
    fun getNextOccurence(): Long
    fun getIntervalInMillis(): Long
    fun serialize(): JsonObject
}