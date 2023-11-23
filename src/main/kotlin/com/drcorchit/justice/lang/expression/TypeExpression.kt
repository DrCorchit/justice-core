package com.drcorchit.justice.lang.expression

import com.drcorchit.justice.game.evaluation.DryRunContext
import com.drcorchit.justice.game.evaluation.EvaluationContext
import com.drcorchit.justice.lang.types.Type
import com.drcorchit.justice.lang.types.TypeType
import com.drcorchit.justice.lang.types.TypedThing
import com.drcorchit.justice.lang.types.source.TypeSource

interface TypeExpression: Expression {

    val type: String

    fun evaluateType(types: TypeSource): Type<*>

    override fun evaluate(context: EvaluationContext): TypedThing<Type<*>> {
        return TypedThing(evaluateType(context.types), TypeType)
    }

    override fun dryRun(context: DryRunContext): Type<*> {
        return TypeType
    }

}