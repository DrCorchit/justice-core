package com.drcorchit.justice.games.events

data class ScheduledEventOutcome(val event: ScheduledEvent, val timestamp: Long, val error: Exception?)