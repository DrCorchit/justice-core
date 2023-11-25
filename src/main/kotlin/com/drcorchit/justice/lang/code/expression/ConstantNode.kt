package com.drcorchit.justice.lang.code.expression

import com.drcorchit.justice.game.evaluation.DryRunContext
import com.drcorchit.justice.game.evaluation.ExecutionContext
import com.drcorchit.justice.lang.types.Type
import com.drcorchit.justice.lang.types.Thing

class ConstantNode(val value: Thing<*>) : Expression {
    constructor(input: Any) : this(Thing.wrapPrimitive(input))

    companion object {
        val NULL = ConstantNode(Thing.UNIT)
        val TRUE = ConstantNode(Thing.TRUE)
        val FALSE = ConstantNode(Thing.FALSE)
        val PI = ConstantNode(Thing.PI)
        val E = ConstantNode(Thing.E)
    }

    override fun run(context: ExecutionContext): Thing<*> {
        return value
    }

    override fun dryRun(context: DryRunContext): Type<*> {
        return value.type
    }
}