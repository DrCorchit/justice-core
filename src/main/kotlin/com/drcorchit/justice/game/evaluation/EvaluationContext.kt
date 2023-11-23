package com.drcorchit.justice.game.evaluation

import com.drcorchit.justice.lang.environment.Environment
import com.drcorchit.justice.lang.types.source.TypeSource

data class EvaluationContext(val types: TypeSource, val env: Environment, val allowMutation: Boolean) {
    fun toDryRunContext(): DryRunContext {
        return DryRunContext(types, env.toTypeEnv())
    }
}