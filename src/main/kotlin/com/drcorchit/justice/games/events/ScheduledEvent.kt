package com.drcorchit.justice.games.events

interface ScheduledEvent {
    val name: String
    fun run()
    fun getLastOccurance(): ScheduledEventOutcome
    fun getNextOccurence(): Long
    fun getIntervalInMillis(): Long
}