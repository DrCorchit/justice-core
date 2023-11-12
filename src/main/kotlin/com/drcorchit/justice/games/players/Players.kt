package com.drcorchit.justice.games.players

import com.drcorchit.justice.games.Game
import com.drcorchit.justice.utils.json.Result

interface Players : Set<Player> {
    val game: Game

    val minPlayerCount: Int
    val maxPlayerCount: Int
    val isJoinable: Boolean get() = game.getState().isJoiningEnabled && size < maxPlayerCount

    fun getPlayer(username: String): Player?
    fun addPlayer(username: String): Result
    fun removePlayer(username: String): Result
}