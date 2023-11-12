package com.drcorchit.justice.games

import com.drcorchit.justice.games.events.DummyEvents
import com.drcorchit.justice.games.events.Events
import com.drcorchit.justice.games.mechanics.DummyMechanics
import com.drcorchit.justice.games.mechanics.Mechanics
import com.drcorchit.justice.games.metadata.JsonMetadata
import com.drcorchit.justice.games.metadata.Metadata
import com.drcorchit.justice.games.players.DummyPlayers
import com.drcorchit.justice.games.players.Player
import com.drcorchit.justice.games.players.Players
import com.drcorchit.justice.games.saving.DummySaving
import com.drcorchit.justice.games.saving.Saving
import com.drcorchit.justice.utils.json.Result
import com.drcorchit.justice.utils.math.Rng
import com.google.gson.JsonObject

class DummyGame: Game {
    private var state = GameState.STAGING

    override fun query(player: Player, query: String): Result {
        return Result.succeed()
    }

    override fun execute(player: Player, command: String): Result {
        return Result.succeed()
    }

    override val events: Events = DummyEvents(this)
    override val mechanics: Mechanics = DummyMechanics(this)
    override val metadata: Metadata = JsonMetadata(this, JsonObject())
    override val players: Players = DummyPlayers(this)
    override val saving: Saving = DummySaving(this)

    override fun setState(state: GameState): Result {
        this.state = state
        return Result.succeed()
    }

    override fun getState(): GameState {
        return state
    }

    override fun describeLatencies(): Result {
        return Result.succeedWithInfo(JsonObject())
    }

    override fun recordLatency(name: String, value: Long) {
        //No-op
    }

    override val random: Rng = Rng()
}