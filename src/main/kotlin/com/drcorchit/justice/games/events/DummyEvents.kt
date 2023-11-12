package com.drcorchit.justice.games.events

import com.drcorchit.justice.games.DummyGame
import com.drcorchit.justice.games.Game
import com.drcorchit.justice.games.players.DummyPlayers
import com.drcorchit.justice.games.players.Player
import com.drcorchit.justice.utils.json.Http.Companion.ok
import com.drcorchit.justice.utils.json.HttpResult
import com.drcorchit.justice.utils.json.Result
import com.google.common.collect.ImmutableMap
import com.google.gson.JsonArray
import com.google.gson.JsonObject

class DummyEvents(override val game: DummyGame) : Events {

    override fun post(player: Player, event: String, info: JsonObject): HttpResult {
        return ok()
    }

    override fun getEventHistory(since: Long): JsonArray {
        return JsonArray()
    }

    override val scheduledEvents: ScheduledEvents =
        object : ScheduledEvents {
            var started = false

            override val game: Game
                get() = this@DummyEvents.game
            override val map: ImmutableMap<String, ScheduledEvent>
                get() = ImmutableMap.of()

            override fun start(): Result {
                return if (started) Result.failWithReason("Scheduled events are already running") else Result.succeed()
            }

            override fun isStarted(): Boolean {
                return started
            }

            override fun stop(): Result {
                return if (!started) Result.failWithReason("Scheduled events are not running") else Result.succeed()
            }

            override fun getHistory(): JsonObject {
                return JsonObject()
            }
        }
}