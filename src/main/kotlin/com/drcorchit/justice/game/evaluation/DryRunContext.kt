package com.drcorchit.justice.game.evaluation

import com.drcorchit.justice.lang.environment.TypeEnv
import com.drcorchit.justice.lang.types.source.TypeSource

data class DryRunContext(val types: TypeSource, val env: TypeEnv)