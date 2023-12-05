package com.drcorchit.justice.game.metadata

import com.drcorchit.justice.game.Game
import com.drcorchit.justice.game.GameState
import com.drcorchit.justice.utils.json.Result
import com.drcorchit.justice.utils.math.Rng
import com.google.gson.JsonObject

abstract class AbstractMetadata(override val parent: Game): Metadata {

    override val random: Rng = Rng()

    protected var gameState: GameState = GameState.PAUSED

    override fun getState(): GameState {
        return gameState
    }

    override fun setState(newState: GameState): Result {
        val oldState = this.gameState
        if (oldState == newState) {
            return Result.failWithReason("The game is already in the $newState state.")
        }

        if (oldState.isEventsEnabled && !newState.isEventsEnabled) {
            parent.events.scheduled.stop()
        } else if (!oldState.isEventsEnabled && newState.isEventsEnabled) {
            parent.events.scheduled.start()
        }

        this.gameState = newState
        parent.metadata.setProperty("state", gameState.name)

        logger.info("setState", "Game $id state changed from $oldState to $newState")

        if (parent.metadata.isAutosaveEnabled) {
            val result = parent.io.save()
            if (!result.success) {
                logger.warn("setState", "Unable to save game $id while changing state: $result")
            }
        }

        val info = JsonObject()
        info.addProperty("gameID", id)
        info.addProperty("message", "Game state changed to $newState")
        info.addProperty("oldState", oldState.name)
        info.addProperty("newState", newState.name)
        parent.notifying.notifyAll(parent.players, "Game State Changed", info)

        return Result.succeedWithInfo(info)
    }
}