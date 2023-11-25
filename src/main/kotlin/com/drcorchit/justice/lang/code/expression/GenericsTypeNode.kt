package com.drcorchit.justice.lang.code.expression

import com.drcorchit.justice.game.evaluation.TypeUniverse
import com.drcorchit.justice.lang.types.Type
import com.google.common.collect.ImmutableList

class GenericsTypeNode(private val primaryType: String, private val paramExprs: ImmutableList<TypeExpression>): TypeExpression {
    override val type = "${primaryType}<${paramExprs.joinToString(", ") { it.type }}>"

    override fun resolveType(types: TypeUniverse): Type<*> {
        val base = types.parseSimpleType(primaryType)
        val params = paramExprs.map { it.resolveType(types) }.let { ImmutableList.copyOf(it) }
        return types.getType(base to params)
    }
}