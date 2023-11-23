package com.drcorchit.justice.lang.expression

import com.drcorchit.justice.lang.types.ArrayType
import com.drcorchit.justice.lang.types.Type
import com.drcorchit.justice.lang.types.source.TypeSource

class ArrayTypeNode(private val eleType: TypeExpression) : TypeExpression {
    override val type = eleType.type + "[]"

    override fun evaluateType(types: TypeSource): Type<*> {
        return ArrayType(this.eleType.evaluateType(types))
    }
}