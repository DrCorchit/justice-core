package com.drcorchit.justice.game.notifications

import com.drcorchit.justice.game.players.Player
import com.drcorchit.justice.utils.logging.Logger
import com.google.gson.JsonObject

private val logger = Logger.getLogger(LogNotifying::class.java)

//Sends notifications without triggering network events
object LogNotifying : Notifying {
    private val history = ArrayList<Notification>()

    override fun notifyAll(players: Set<Player>, name: String, info: JsonObject) {
        logger.info("notifyAll", "Notification issued: $name ---> $players")
        history.add(Notification(name, players, System.currentTimeMillis()))
    }

    override fun getHistory(since: Long): List<Notification> {
        return history
    }
}