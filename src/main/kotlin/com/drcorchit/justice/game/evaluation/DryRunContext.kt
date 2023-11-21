package com.drcorchit.justice.game.evaluation

import com.drcorchit.justice.game.Game
import com.drcorchit.justice.lang.environment.TypeEnv

data class DryRunContext(val game: Game, val env: TypeEnv)