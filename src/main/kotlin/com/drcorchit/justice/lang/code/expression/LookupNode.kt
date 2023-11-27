package com.drcorchit.justice.lang.code.expression

import com.drcorchit.justice.game.evaluation.context.DryRunContext
import com.drcorchit.justice.game.evaluation.context.ExecutionContext
import com.drcorchit.justice.lang.members.StaticMember
import com.drcorchit.justice.lang.types.MemberType
import com.drcorchit.justice.lang.code.Thing
import com.drcorchit.justice.lang.types.Type
import com.google.common.collect.ImmutableList

class LookupNode(
    private val base: Expression?,
    private val id: String,
    //null args means no parens
    //empty args means empty parens ()
    private val parameters: ImmutableList<Expression>
) : Expression {
    override fun run(context: ExecutionContext): Thing<*> {
        val args = parameters.map { it.run(context).value }
        return if (base == null) {
            val result = context.lookup(id)!!
            when (val thing = result.value) {
                is StaticMember -> thing.applyAndWrap(args)
                else -> result
            }
        } else {
            base.run(context).runMember(id, args)
        }
    }

    override fun dryRun(context: DryRunContext): Type<*> {
        val args = parameters.map { it.dryRun(context) }
        return if (base == null) {
            val result = context.lookup(id)!!
            when (result) {
                //TODO figure this out. Should return return type of member.
                MemberType -> result
                else -> result
            }
        } else {
            base.dryRun(context).getMember(id)!!.returnType
        }
    }
}