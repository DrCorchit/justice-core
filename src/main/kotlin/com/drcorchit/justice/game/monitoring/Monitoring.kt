package com.drcorchit.justice.game.monitoring

import com.drcorchit.justice.game.Game
import com.drcorchit.justice.utils.json.Result

interface Monitoring {
    fun describeLatencies(game: Game): Result
    fun recordLatency(game: Game, name: String, value: Long)
}