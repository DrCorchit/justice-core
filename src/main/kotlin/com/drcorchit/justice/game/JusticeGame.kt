package com.drcorchit.justice.game

import com.drcorchit.justice.game.events.EventsImpl
import com.drcorchit.justice.game.mechanics.MechanicsImpl
import com.drcorchit.justice.game.metadata.JsonMetadata
import com.drcorchit.justice.game.monitoring.Monitoring
import com.drcorchit.justice.game.notifications.Notifying
import com.drcorchit.justice.game.players.Player
import com.drcorchit.justice.game.players.PlayersImpl
import com.drcorchit.justice.game.saving.Saving
import com.drcorchit.justice.utils.json.Result
import com.drcorchit.justice.utils.json.Result.Companion.failWithReason
import com.drcorchit.justice.utils.json.Result.Companion.succeedWithInfo
import com.drcorchit.justice.utils.math.Rng
import com.google.gson.JsonObject

//Justice game is able to fulfill all the responsibilities of a game on its own, except
//Notifying players and saving, which may require network calls.
class JusticeGame(
    override val notifying: Notifying,
    override val saving: Saving,
    override val monitoring: Monitoring
) : Game {
    override val players = PlayersImpl(this)
    override val mechanics = MechanicsImpl(this)
    override val events = EventsImpl(this)
    override val metadata: JsonMetadata = JsonMetadata(this)

    override val random = Rng(metadata.getProperty("seed")?.asLong ?: System.currentTimeMillis())

    override fun query(player: Player, query: String): Result {
        TODO("Not yet implemented")
    }

    override fun execute(player: Player, command: String): Result {
        TODO("Not yet implemented")
    }

    private var state = GameState.PAUSED
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
            val result = saving.save(this)
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
        TODO("Not yet implemented")
    }

    override fun deserialize(info: JsonObject) {
        TODO("Not yet implemented")
    }
}