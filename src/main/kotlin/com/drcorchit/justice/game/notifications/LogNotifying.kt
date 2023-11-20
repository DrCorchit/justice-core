package com.drcorchit.justice.game.notifications

import com.drcorchit.justice.game.players.Player
import com.google.gson.JsonObject

private val logger = com.drcorchit.justice.utils.logging.Logger.getLogger(LogNotifying::class.java)

//Sends notifications without triggering network events
class LogNotifying : Notifying {
    private val history = ArrayList<Notification>()

    override fun notifyAll(players: Set<Player>, name: String, info: JsonObject) {
        logger.info("notifyAll", "Notification issued: $name ---> $players")
        history.add(Notification(name, players, System.currentTimeMillis()))
    }

    override fun getHistory(since: Long): List<Notification> {
        return history
    }
}