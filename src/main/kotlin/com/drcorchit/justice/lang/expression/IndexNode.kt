package com.drcorchit.justice.lang.expression

import com.drcorchit.justice.exceptions.MemberDefinitionException
import com.drcorchit.justice.exceptions.MemberNotFoundException
import com.drcorchit.justice.exceptions.TypeException
import com.drcorchit.justice.game.evaluation.DryRunContext
import com.drcorchit.justice.game.evaluation.EvaluationContext
import com.drcorchit.justice.lang.evaluators.Evaluator
import com.drcorchit.justice.lang.members.Member

class IndexNode(private val arrayExpr: Expression, private val indexExpr: Expression): Expression {
    override fun evaluate(context: EvaluationContext): Any {
        val array = arrayExpr.evaluate(context)!!
        val index = indexExpr.evaluate(context)!!

        val type = context.game.types.getType(array)
        val member = type?.getMember("get") as Member<Any>
        return member.apply(array, listOf(index))!!
    }

    override fun dryRun(context: DryRunContext): Evaluator<*> {
        val arrayType = arrayExpr.dryRun(context)
        val member = arrayType.getMember("get") ?: throw MemberNotFoundException(arrayType, "get")
        val expectedType = member.argTypes[1]
        val actualType = indexExpr.dryRun(context)
        if (!expectedType.accept(actualType)) {
            throw TypeException("get", expectedType, actualType)
        }
        if (member.returnType == null) {
            throw MemberDefinitionException("Method \"get\" must return a non-null type.")
        }
        return member.returnType!!
    }
}