package com.drcorchit.justice.lang.code.expression

import com.drcorchit.justice.game.evaluation.context.DryRunContext
import com.drcorchit.justice.game.evaluation.context.ExecutionContext
import com.drcorchit.justice.game.evaluation.universe.TypeUniverse
import com.drcorchit.justice.lang.code.Thing
import com.drcorchit.justice.lang.types.Type
import com.drcorchit.justice.lang.types.TypeType

interface TypeExpression: Expression {

    val type: String

    fun resolveType(types: TypeUniverse): Type<*>

    override fun run(context: ExecutionContext): Thing<Type<*>> {
        return Thing(resolveType(context.universe), TypeType)
    }

    override fun dryRun(context: DryRunContext): Type<*> {
        return TypeType
    }

}