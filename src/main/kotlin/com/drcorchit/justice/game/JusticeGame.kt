package com.drcorchit.justice.game

import com.drcorchit.justice.game.evaluation.JusticeTypes
import com.drcorchit.justice.game.events.EventsImpl
import com.drcorchit.justice.game.io.IO
import com.drcorchit.justice.game.mechanics.MechanicsImpl
import com.drcorchit.justice.game.metadata.JsonMetadata
import com.drcorchit.justice.game.monitoring.Monitoring
import com.drcorchit.justice.game.notifications.Notifying
import com.drcorchit.justice.game.players.PlayersImpl
import com.drcorchit.justice.utils.json.Result
import com.drcorchit.justice.utils.json.Result.Companion.failWithReason
import com.drcorchit.justice.utils.json.Result.Companion.succeedWithInfo
import com.drcorchit.justice.utils.json.TimestampedJson
import com.drcorchit.justice.utils.json.info
import com.drcorchit.justice.utils.json.lastModified
import com.drcorchit.justice.utils.math.Rng
import com.google.gson.JsonObject

//Justice game is able to fulfill all the responsibilities of a game on its own, except
//Notifying players and saving, which may require network calls.
class JusticeGame(
    override val notifying: Notifying,
    override val io: IO,
    override val monitoring: Monitoring
) : Game {
    override val players = PlayersImpl(this)
    override val mechanics = MechanicsImpl(this)
    override val events = EventsImpl(this)
    override val metadata = JsonMetadata(this)
    override val types = JusticeTypes(this)
    override val random = Rng(metadata.getProperty("seed")?.asLong ?: System.currentTimeMillis())

    private var state = GameState.PAUSED

    init {
        metadata.setProperty("state", state.name)
    }

    override fun setState(newState: GameState): Result {
        val oldState = this.state
        if (oldState == newState) {
            return failWithReason("The game is already in the $newState state.")
        }

        if (oldState.isEventsEnabled && !newState.isEventsEnabled) {
            events.scheduled.stop()
        } else if (!oldState.isEventsEnabled && newState.isEventsEnabled) {
            events.scheduled.start()
        }

        this.state = newState
        metadata.setProperty("state", state.name)

        logger.info("setState", "Game $id state changed from $oldState to $newState")

        if (metadata.isAutosaveEnabled) {
            val result = io.save(this)
            if (!result.success) {
                logger.warn("setState", "Unable to save game $id while changing state: $result")
            }
        }

        val info = JsonObject()
        info.addProperty("gameID", id)
        info.addProperty("message", "Game state changed to $newState")
        info.addProperty("oldState", oldState.name)
        info.addProperty("newState", newState.name)
        notifying.notifyAll(players, "Game State Changed", info)

        return succeedWithInfo(info)
    }

    override fun getState(): GameState {
        return state
    }

    override fun serialize(): JsonObject {
        val output = JsonObject()
        output.add("players", players.serialize())
        output.add("mechanics", mechanics.serialize())
        output.add("events", events.serialize())
        output.add("metadata", metadata.serialize())
        return output
    }

    override fun deserialize(info: TimestampedJson) {
        players.deserialize(info.info.asJsonObject.getAsJsonObject("players"))
        mechanics.deserialize(info.info.asJsonObject.getAsJsonObject("mechanics"), info.lastModified)
        events.deserialize(info.info.asJsonObject.getAsJsonObject("events"))
        metadata.deserialize(info.info.asJsonObject.getAsJsonObject("metadata"))
    }
}