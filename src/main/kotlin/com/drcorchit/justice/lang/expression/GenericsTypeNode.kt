package com.drcorchit.justice.lang.expression

import com.drcorchit.justice.lang.types.Type
import com.drcorchit.justice.lang.types.source.TypeSource

class GenericsTypeNode(val primaryType: String, val args: List<TypeExpression>): TypeExpression {
    override val type = "${primaryType}<${args.joinToString(", ") { it.type }}>"

    override fun evaluateType(types: TypeSource): Type<*>? {
        val base = Class.forName(primaryType).kotlin
        return types.getType(base, args.map { it.evaluateType(types)!! })
    }
}