package com.drcorchit.justice.lang.code.expression

import com.drcorchit.justice.game.evaluation.universe.TypeUniverse
import com.drcorchit.justice.lang.types.ArrayType
import com.drcorchit.justice.lang.types.Type

class ArrayTypeNode(private val eleType: TypeExpression) : TypeExpression {
    override val type = eleType.type + "[]"

    override fun resolveType(types: TypeUniverse): Type<*> {
        return ArrayType(this.eleType.resolveType(types))
    }

    override fun toString(): String {
        return "$eleType[]"
    }
}