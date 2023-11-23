package com.drcorchit.justice.lang.expression

import com.drcorchit.justice.lang.types.Type
import com.drcorchit.justice.lang.types.source.TypeSource

class TypeNode(override val type: String) : TypeExpression {
    override fun evaluateType(types: TypeSource): Type<*>? {
        return types.getType(type)
    }
}