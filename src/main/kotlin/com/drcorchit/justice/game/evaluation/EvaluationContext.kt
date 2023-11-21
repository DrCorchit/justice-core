package com.drcorchit.justice.game.evaluation

import com.drcorchit.justice.game.Game
import com.drcorchit.justice.lang.environment.Environment

data class EvaluationContext(val game: Game, val env: Environment, val allowMutation: Boolean) {
    fun toDryRunContext(): DryRunContext {
        return DryRunContext(game, env.toTypeEnv())
    }
}