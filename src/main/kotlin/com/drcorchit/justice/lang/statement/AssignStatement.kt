package com.drcorchit.justice.lang.statement

import com.drcorchit.justice.exceptions.JusticeException
import com.drcorchit.justice.game.evaluation.DryRunContext
import com.drcorchit.justice.game.evaluation.EvaluationContext
import com.drcorchit.justice.lang.evaluators.Evaluator
import com.drcorchit.justice.lang.expression.Expression
import com.drcorchit.justice.lang.members.DataFieldMember

sealed class AssignStatement(private val rhv: Expression) : Statement {
    override fun execute(context: EvaluationContext): Any? {
        val newValue = rhv.evaluate(context)!!
        assign(newValue, context)
        return newValue
    }

    override fun dryRun(context: DryRunContext): Evaluator<*>? {
        val actualType = rhv.dryRun(context)
        //TODO
        return null
    }

    abstract fun assign(newValue: Any, context: EvaluationContext)

    class LocalAssign(rhv: Expression, val id: String) : AssignStatement(rhv) {
        override fun assign(newValue: Any, context: EvaluationContext) {
            context.env.assign(id, newValue)
        }
    }

    class InstanceAssign(private val lhv: Expression, rhv: Expression, private val memberName: String) : AssignStatement(rhv) {
        override fun assign(newValue: Any, context: EvaluationContext) {
            check(context.allowMutation)
            val instance = lhv.evaluate(context)!!
            val type = context.game.types.getType(instance)
            val member = type!!.getMember(memberName)!!
            if (member is DataFieldMember<*>) {
                member.setCast(instance, newValue)
            }
        }
    }

    class IndexAssign(private val lhv: Expression, rhv: Expression, private val indexExpr: Expression) : AssignStatement(rhv) {
        override fun assign(newValue: Any, context: EvaluationContext) {
            check(context.allowMutation)
            val arrayListOrMap = lhv.evaluate(context)!!
            val indexOrKey = indexExpr.evaluate(context)
            val type = context.game.types.getType(arrayListOrMap)!!
            val set = type.getMember("set")
            val put = type.getMember("put")
            if (set != null) {
                set.applyCast(arrayListOrMap, listOf(indexOrKey, newValue))
            } else if (put != null) {
                put.applyCast(arrayListOrMap, listOf(indexOrKey, newValue))
            } else {
                throw JusticeException("Instance $lhv has no member suitable for index assignment.")
            }
        }
    }
}