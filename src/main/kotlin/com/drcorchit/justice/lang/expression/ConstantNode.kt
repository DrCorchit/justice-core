package com.drcorchit.justice.lang.expression

import com.drcorchit.justice.game.evaluation.DryRunContext
import com.drcorchit.justice.game.evaluation.EvaluationContext
import com.drcorchit.justice.lang.types.Type
import com.drcorchit.justice.lang.types.TypedThing

class ConstantNode(val value: TypedThing<*>) : Expression {
    constructor(input: Any) : this(TypedThing.wrapPrimitive(input))

    companion object {
        val TRUE = ConstantNode(TypedThing.TRUE)
        val FALSE = ConstantNode(TypedThing.FALSE)
        val PI = ConstantNode(TypedThing.PI)
        val E = ConstantNode(TypedThing.E)
    }

    override fun evaluate(context: EvaluationContext): TypedThing<*> {
        return value
    }

    override fun dryRun(context: DryRunContext): Type<*> {
        return value.type
    }
}