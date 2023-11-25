package com.drcorchit.justice.lang.code.statement

import com.drcorchit.justice.exceptions.JusticeException
import com.drcorchit.justice.exceptions.MemberNotFoundException
import com.drcorchit.justice.exceptions.TypeException
import com.drcorchit.justice.game.evaluation.DryRunContext
import com.drcorchit.justice.game.evaluation.ExecutionContext
import com.drcorchit.justice.lang.code.expression.Expression
import com.drcorchit.justice.lang.members.DataFieldMember
import com.drcorchit.justice.lang.types.Thing
import com.drcorchit.justice.lang.types.Type

sealed class AssignStatement(private val rhv: Expression) : Statement {
    override fun run(context: ExecutionContext): Thing<*> {
        val newValue = rhv.run(context)
        assign(newValue, context)
        return newValue
    }

    override fun dryRun(context: DryRunContext): Type<*> {
        val newType = rhv.dryRun(context)
        assignDry(newType, context)
        return newType
    }

    abstract fun assign(newValue: Any, context: ExecutionContext)

    abstract fun assignDry(newValue: Type<*>, context: DryRunContext)

    class LocalAssign(rhv: Expression, val id: String) : AssignStatement(rhv) {
        override fun assign(newValue: Any, context: ExecutionContext) {
            context.assign(id, newValue)
        }

        override fun assignDry(newValue: Type<*>, context: DryRunContext) {
            context.assign(id, newValue)
        }
    }

    class InstanceAssign(private val lhv: Expression, rhv: Expression, private val memberName: String) : AssignStatement(rhv) {
        override fun assign(newValue: Any, context: ExecutionContext) {
            check(!context.sideEffectsDisabled) { "Side effects are disabled in the current execution context." }
            val instance = lhv.run(context)
            instance.assignMember(memberName, newValue)
        }

        override fun assignDry(newValue: Type<*>, context: DryRunContext) {
            check(!context.sideEffectsDisabled) { "Side effects are disabled in the current execution context." }
            val instanceType = lhv.dryRun(context)
            val member = instanceType.getMember(memberName)
            if (member == null) {
                throw MemberNotFoundException(instanceType.clazz, memberName)
            } else if (member is DataFieldMember<*>) {
                if (!member.mutable) {
                    throw JusticeException("Attempting to assign immutable member of ${instanceType.clazz}")
                } else if (!member.returnType.accept(newValue)) {
                    throw TypeException(memberName, member.returnType, newValue)
                }
            } else {
                throw JusticeException("Cannot assign a non-data member.")
            }
        }
    }

    class IndexAssign(private val lhv: Expression, rhv: Expression, private val indexExpr: Expression) : AssignStatement(rhv) {
        override fun assign(newValue: Any, context: ExecutionContext) {
            check(!context.sideEffectsDisabled) { "Side effects are disabled in the current execution context." }
            val arrayListOrMap = lhv.run(context)
            val indexOrKey = indexExpr.run(context)
            arrayListOrMap.setOrPut(indexOrKey, newValue)
        }

        override fun assignDry(newValue: Type<*>, context: DryRunContext) {
            check(!context.sideEffectsDisabled) { "Side effects are disabled in the current execution context." }
            //TODO
        }
    }
}