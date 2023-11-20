package com.drcorchit.justice.game.monitoring

import com.drcorchit.justice.game.Game
import com.drcorchit.justice.utils.Utils.createCache
import com.drcorchit.justice.utils.json.JsonUtils.toJsonObject
import com.drcorchit.justice.utils.json.Result
import com.drcorchit.justice.utils.logging.LatencyMonitor
import java.util.*

class LatencyMonitoring : Monitoring {
    private val cache = createCache<String, GameMonitor>(100) { GameMonitor() }

    private class GameMonitor {
        private val monitors = TreeMap<String, LatencyMonitor>()

        fun describeLatencies(): Result {
            val info = monitors.mapValues { it.value.serialize() }.toJsonObject()
            return Result.succeedWithInfo(info)
        }

        fun recordLatency(name: String, value: Long) {
            monitors.computeIfAbsent(name) { LatencyMonitor(name) }.observeLatency(value)
        }
    }

    override fun describeLatencies(game: Game): Result {
        return cache.get(game.id).describeLatencies()
    }

    override fun recordLatency(game: Game, name: String, value: Long) {
        return cache.get(game.id).recordLatency(name, value)
    }
}