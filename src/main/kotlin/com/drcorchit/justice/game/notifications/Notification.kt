package com.drcorchit.justice.game.notifications

import com.drcorchit.justice.game.players.Player

data class Notification(
    val name: String,
    val players: Set<Player>,
    val timestamp: Long = System.currentTimeMillis()
)