package com.drcorchit.justice.lang.code.expression

import com.drcorchit.justice.exceptions.MemberDefinitionException
import com.drcorchit.justice.exceptions.MemberNotFoundException
import com.drcorchit.justice.exceptions.TypeException
import com.drcorchit.justice.game.evaluation.DryRunContext
import com.drcorchit.justice.game.evaluation.ExecutionContext
import com.drcorchit.justice.lang.code.Thing
import com.drcorchit.justice.lang.types.Type
import com.drcorchit.justice.lang.types.UnitType

class IndexNode(private val arrayExpr: Expression, private val indexExpr: Expression): Expression {
    override fun run(context: ExecutionContext): Thing<*> {
        val array = arrayExpr.run(context)
        val index = indexExpr.run(context)
        return array.runMember("get", listOf(index))
    }

    override fun dryRun(context: DryRunContext): Type<*> {
        val arrayType = arrayExpr.dryRun(context)
        val member = arrayType.getMember("get") ?: throw MemberNotFoundException(arrayType.clazz, "get")
        val expectedType = member.argTypes[1]
        val actualType = indexExpr.dryRun(context)
        if (!expectedType.accept(actualType)) {
            throw TypeException("get", expectedType, actualType)
        }
        if (member.returnType == UnitType) {
            throw MemberDefinitionException("Method \"get\" must return a non-unit type.")
        }
        return member.returnType
    }
}