package com.drcorchit.justice.game.notifications

import com.drcorchit.justice.game.players.Player
import com.google.gson.JsonObject

interface Notifying {

    fun notify(player: Player, name: String, info: JsonObject) {
        notifyAll(setOf(player), name, info)
    }

    fun notifyAll(players: Set<Player>, name: String, info: JsonObject)

    fun getHistory(since: Long): List<Notification>
}