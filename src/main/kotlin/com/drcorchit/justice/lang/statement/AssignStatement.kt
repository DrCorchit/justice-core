package com.drcorchit.justice.lang.statement

import com.drcorchit.justice.game.evaluation.DryRunContext
import com.drcorchit.justice.game.evaluation.EvaluationContext
import com.drcorchit.justice.lang.expression.Expression
import com.drcorchit.justice.lang.types.Type
import com.drcorchit.justice.lang.types.TypedThing

sealed class AssignStatement(private val rhv: Expression) : Statement {
    override fun execute(context: EvaluationContext): TypedThing<*> {
        val newValue = rhv.evaluate(context)
        assign(newValue, context)
        return newValue
    }

    override fun dryRun(context: DryRunContext): Type<*> {
        val newType = rhv.dryRun(context)
        assignDry(newType, context)
        return newType
    }

    abstract fun assign(newValue: Any, context: EvaluationContext)

    abstract fun assignDry(newValue: Type<*>, context: DryRunContext)

    class LocalAssign(rhv: Expression, val id: String) : AssignStatement(rhv) {
        override fun assign(newValue: Any, context: EvaluationContext) {
            context.env.assign(id, newValue)
        }

        override fun assignDry(newValue: Type<*>, context: DryRunContext) {
            context.env.assignDry(id, newValue)
        }
    }

    class InstanceAssign(private val lhv: Expression, rhv: Expression, private val memberName: String) : AssignStatement(rhv) {
        override fun assign(newValue: Any, context: EvaluationContext) {
            check(context.allowMutation)
            val instance = lhv.evaluate(context)
            instance.assignMember(memberName, newValue)
        }

        override fun assignDry(newValue: Type<*>, context: DryRunContext) {
            TODO("Not yet implemented")
        }
    }

    class IndexAssign(private val lhv: Expression, rhv: Expression, private val indexExpr: Expression) : AssignStatement(rhv) {
        override fun assign(newValue: Any, context: EvaluationContext) {
            check(context.allowMutation)
            val arrayListOrMap = lhv.evaluate(context)
            val indexOrKey = indexExpr.evaluate(context)
            arrayListOrMap.setOrPut(indexOrKey, newValue)
        }

        override fun assignDry(newValue: Type<*>, context: DryRunContext) {
            TODO("Not yet implemented")
        }
    }
}