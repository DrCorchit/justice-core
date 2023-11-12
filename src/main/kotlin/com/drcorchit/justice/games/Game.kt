package com.drcorchit.justice.games

import com.drcorchit.justice.games.events.Events
import com.drcorchit.justice.games.mechanics.Mechanics
import com.drcorchit.justice.games.metadata.JsonMetadata
import com.drcorchit.justice.games.metadata.Metadata
import com.drcorchit.justice.games.players.Player
import com.drcorchit.justice.games.players.Players
import com.drcorchit.justice.games.saving.Saving
import com.drcorchit.justice.utils.json.*
import com.drcorchit.justice.utils.math.Rng
import com.google.gson.JsonObject
import com.google.gson.JsonPrimitive
import java.time.LocalDate
import java.util.*

interface Game {
    val id: String get() = metadata.id

    fun query(player: Player, query: String): Result

    fun execute(player: Player, command: String): Result

    //Events Sub-API
    val events: Events

    //Mechanics Sub-API
    val mechanics: Mechanics

    //Metadata Sub-API
    val metadata: Metadata

    //Players Sub-API
    val players: Players

    //Saving Sub-API
    val saving: Saving

    //State Sub-API
    fun setState(state: GameState): Result
    fun getState(): GameState

    //Monitoring Sub-API
    fun describeLatencies(): Result
    fun recordLatency(name: String, value: Long)

    //RNG Sub-API
    val random: Rng
    var seed: Long
        get() = random.getSeed()
        set(seed) = random.setSeed(seed)

    fun summarize(): JsonObject {
        val info = JsonObject()
        info.addProperty("id", metadata.id)
        info.addProperty("name", metadata.name)
        info.addProperty("description", metadata.description)
        info.addProperty("author", metadata.author)
        info.addProperty("version", metadata.version.value)
        info.addProperty("image", metadata.image)
        info.addProperty("start", metadata.start)
        info.addProperty("lastModified", metadata.lastModified)
        info.addProperty("state", getState().name)
        info.addProperty("isJoinable", players.isJoinable)
        info.addProperty("minPlayers", players.minPlayerCount)
        info.addProperty("maxPlayers", players.maxPlayerCount)
        info.add("players", players
            .filter { it.human }
            .map { JsonPrimitive(it.name) }
            .toJsonArray())
        return info
    }

    companion object {
        @JvmStatic
        fun generateId(): String {
            val date = LocalDate.now()
            val num = Random().nextInt(1000000)
            return "game_${date.year}_${date.monthValue}_${date.dayOfMonth}_$num"
        }
    }
}