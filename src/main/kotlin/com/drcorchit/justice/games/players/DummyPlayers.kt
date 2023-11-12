package com.drcorchit.justice.games.players

import com.drcorchit.justice.games.DummyGame
import com.drcorchit.justice.utils.json.Result

class DummyPlayers(override val game: DummyGame) : Players {
    private val roster = LinkedHashMap<String, DummyPlayer>()

    override val minPlayerCount: Int = 3
    override val maxPlayerCount: Int = 10
    override fun getPlayer(username: String): DummyPlayer? {
        return roster[username]
    }

    override fun addPlayer(username: String): Result {
        if (roster.containsKey(username)) return Result.failWithReason("That player is already in the game.")
        roster[username] = DummyPlayer(username, false, false)
        return Result.succeed()
    }

    override fun removePlayer(username: String): Result {
        return if (roster.remove(username) == null) {
            Result.failWithReason("The player is not in the game.")
        } else {
            Result.succeed()
        }
    }

    override val size: Int
        get() = roster.size

    override fun contains(element: Player): Boolean {
        return roster.values.contains(element)
    }

    override fun containsAll(elements: Collection<Player>): Boolean {
        return roster.values.containsAll(elements)
    }

    override fun isEmpty(): Boolean {
        return roster.isEmpty()
    }

    override fun iterator(): Iterator<Player> {
        return roster.values.iterator()
    }
}