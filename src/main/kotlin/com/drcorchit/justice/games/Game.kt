package com.drcorchit.justice.games

import com.drcorchit.utils.Version
import com.drcorchit.utils.json.*
import com.drcorchit.utils.math.Rng
import com.google.errorprone.annotations.CanIgnoreReturnValue
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import com.google.gson.JsonPrimitive
import java.time.LocalDate
import java.util.*
import java.util.function.Consumer
import java.util.function.Function

interface Game {
    //Location from which the game is saved and loaded.
    val path: String

    fun load(info: JsonObject)

    fun has(mechanic: String): Boolean

    //Throws NoSuchElementException if not found.
    operator fun <T : GameMechanic<*>> get(mechanic: String): T

    //Convenience method
    fun <K : GameMechanic<*>, V> with(mechanic: String, task: Function<K, V>, def: V? = null): V {
        return if (has(mechanic)) task.apply(get(mechanic))
        else def ?: throw IllegalStateException("Mechanic $mechanic not found in game ${id()}.")
    }

    //Convenience method
    @CanIgnoreReturnValue
    fun <T : GameMechanic<*>> doWith(mechanic: String, task: Consumer<T>): Boolean {
        return if (has(mechanic)) {
            task.accept(get(mechanic))
            return true
        } else false
    }

    fun copy(): Game

    fun serializeForPlayer(player: Player, mutableOnly: Boolean): Result

    fun autosave(): Result {
        return if (isAutosaveEnabled) save() else failWithReason("Autosave has been disabled in the game configuration.")
    }

    //Saving may be triggered after creation, after a state change, or after an api call.
    fun save(): Result

    //Irreversibly deletes files or database entries associated with the game.
    fun delete(): Result

    val start: Long

    val age: Long
        get() = System.currentTimeMillis() - start

    val lastModified: Long

    fun query(username: String, query: String): Result

    fun execute(username: String, command: String): Result

    fun postEvent(username: String, event: String, info: JsonObject): Result

    fun getEventHistory(since: Long): JsonArray

    //Metadata Sub-API
    //Don't modify the "id" metadata field of a hosted game.
    val metadata: JsonObject

    fun id(): String {
        return metadata["id"].asString
    }

    fun name(): String {
        return metadata["name"].asString
    }

    fun description(): String {
        return metadata["description"].asString
    }

    fun author(): String {
        return metadata["author"].asString
    }

    fun version(): Version {
        return Version(metadata["version"].asString)
    }

    val isAutosaveEnabled: Boolean
        get() = metadata.getBool("autosaveEnabled", false)
    val isNotificationsEnabled: Boolean
        get() = metadata.getBool("notificationsEnabled", false)

    fun summarize(): JsonObject {
        val info = JsonObject()
        info.addProperty("id", id())
        info.addProperty("name", name())
        info.addProperty("description", description())
        info.addProperty("author", author())
        info.addProperty("version", version().toString())
        val metadata = metadata
        if (metadata.has("image")) {
            info.add("image", metadata["image"])
        }
        info.addProperty("start", start)
        info.addProperty("lastModified", lastModified)
        info.addProperty("state", state.name)
        info.addProperty("isJoinable", isJoinable)
        info.addProperty("minPlayers", minPlayerCount)
        info.addProperty("maxPlayers", maxPlayerCount)
        info.add("players", getPlayers
            .filter { it.human }
            .map { it.name() }
            .map { JsonPrimitive(it) }
            .toJsonArray())
        return info
    }

    //Players Sub-API
    fun getPlayer(username: String): Player
    val getPlayers: Set<Player>
    val playerCount: Int get() = getPlayers.size
    val minPlayerCount: Int
    val maxPlayerCount: Int
    fun addPlayer(username: String, moderator: Boolean): Result
    fun removePlayer(username: String): Result

    //State Sub-API
    fun setState(state: GameState?): Result
    val state: GameState
    val isJoinable: Boolean get() = state.isJoiningEnabled && playerCount < maxPlayerCount

    //Monitoring Sub-API
    fun describeLatencies(): Result
    fun observeLatency(name: String, value: Long)

    //RNG Sub-API
    val random: Rng
    var seed: Long
        get() = random.getSeed()
        set(seed: Long) = random.setSeed(seed)

    companion object {
        @JvmStatic
        fun generateId(): String {
            val date = LocalDate.now()
            val num = Random().nextInt(1000000)
            return "game_${date.year}_${date.monthValue}_${date.dayOfMonth}_$num"
        }
    }
}