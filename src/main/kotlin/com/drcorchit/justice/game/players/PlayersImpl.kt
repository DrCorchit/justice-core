package com.drcorchit.justice.game.players

import com.drcorchit.justice.game.Game
import com.drcorchit.justice.utils.json.JsonUtils
import com.drcorchit.justice.utils.json.JsonUtils.toJsonArray
import com.drcorchit.justice.utils.json.Result
import com.google.gson.JsonObject
import java.util.*

class PlayersImpl(override val parent: Game) : Players {
    private val playersByID = LinkedHashMap<String, Player>()
    private val playersByName = TreeMap<String, Player>()
    private var min: Int = 2
    private var max: Int = 10

    data class PlayerImpl(
        override var id: String,
        override var name: String,
        override var moderator: Boolean,
        override var human: Boolean
    ) : Player {
        override fun toString(): String {
            return name
        }
    }

    override val minPlayerCount: Int
        get() = min
    override val maxPlayerCount: Int
        get() = max


    override fun getPlayer(usernameOrID: String): Player? {
        return playersByID[usernameOrID] ?: playersByName[usernameOrID]
    }


    override fun addPlayer(player: Player): Result {
        return if (playersByID.containsKey(player.id)) {
            Result.failWithReason("${player.name} has already joined game ${parent.id}.")
        } else if (size >= maxPlayerCount) {
            Result.failWithReason("Game ${parent.id} is full.")
        } else {
            playersByID[player.id] = player
            playersByName[player.name] = player
            Result.succeed()
        }
    }

    override fun removePlayer(player: Player): Result {
        return if (playersByID.remove(player.id) != null) {
            playersByName.remove(player.name)
            Result.succeed()
        } else {
            Result.failWithReason("No player named ${player.name} found in game ${parent.id}")
        }
    }

    override fun serialize(): JsonObject {
        val output = JsonObject()
        output.addProperty("minPlayerCount", minPlayerCount)
        output.addProperty("maxPlayerCount", maxPlayerCount)
        output.add("players", playersByID.values.map { PlayerType.serialize(it) }.toJsonArray())
        return output
    }

    override fun deserialize(info: JsonObject) {
        min = info["minPlayerCount"].asInt
        max = info["maxPlayerCount"].asInt
        info.getAsJsonArray("players")
            .map { JsonUtils.GSON.fromJson(info, PlayerImpl::class.java) }
            .forEach { playersByID[it.id] = it; playersByName[it.name] = it }
    }


    override val size: Int
        get() = playersByID.size

    override fun contains(element: Player): Boolean {
        return playersByID.values.contains(element)
    }

    override fun containsAll(elements: Collection<Player>): Boolean {
        return playersByID.values.containsAll(elements)
    }

    override fun isEmpty(): Boolean {
        return playersByID.isEmpty()
    }

    override fun iterator(): Iterator<Player> {
        return playersByID.values.iterator()
    }
}