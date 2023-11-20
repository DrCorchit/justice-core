package com.drcorchit.justice.game

import com.drcorchit.justice.game.events.Events
import com.drcorchit.justice.game.mechanics.Mechanics
import com.drcorchit.justice.game.metadata.Metadata
import com.drcorchit.justice.game.monitoring.Monitoring
import com.drcorchit.justice.game.notifications.Notifying
import com.drcorchit.justice.game.players.Player
import com.drcorchit.justice.game.players.Players
import com.drcorchit.justice.game.saving.Saving
import com.drcorchit.justice.utils.json.JsonUtils.toJsonArray
import com.drcorchit.justice.utils.json.Result
import com.drcorchit.justice.utils.logging.HasUri
import com.drcorchit.justice.utils.logging.Uri
import com.drcorchit.justice.utils.math.Rng
import com.google.gson.JsonObject
import com.google.gson.JsonPrimitive
import java.time.LocalDate
import java.util.*

interface Game: HasUri {
    val id: String get() = metadata.id
    override val uri get() = Uri(null, id)
    override val parent get() = null

    fun query(player: Player, query: String): Result
    fun execute(player: Player, command: String): Result
    fun setState(newState: GameState): Result
    fun getState(): GameState

    fun serialize(): JsonObject {
        val output = JsonObject()
        //ID and State should be saved within metadata.
        output.add("metadata", metadata.serialize())
        output.add("players", players.serialize())
        output.add("mechanics", mechanics.serialize())
        output.add("events", events.serialize())
        return output
    }

    fun deserialize(info: JsonObject)

    val random: Rng
    var seed: Long
        get() = random.getSeed()
        set(seed) = random.setSeed(seed)

    //Various Sub-APIs:
    val players: Players
    val mechanics: Mechanics
    val events: Events
    val metadata: Metadata
    val notifying: Notifying
    val saving: Saving
    val monitoring: Monitoring

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
        info.addProperty("isJoinable", metadata.isJoinable)
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