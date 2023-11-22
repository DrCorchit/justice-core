package com.drcorchit.justice.lang.expression

import com.drcorchit.justice.exceptions.MemberNotFoundException
import com.drcorchit.justice.exceptions.ReturnException
import com.drcorchit.justice.game.evaluation.DryRunContext
import com.drcorchit.justice.game.evaluation.EvaluationContext
import com.drcorchit.justice.lang.evaluators.Evaluator
import com.drcorchit.justice.lang.members.FieldMember
import com.google.common.collect.ImmutableList

class LookupNode(
    private val base: Expression?,
    private val name: String,
    //null args means no parens
    //empty args means empty parens ()
    private val args: ImmutableList<Expression>?
) : Expression {
    override fun evaluate(context: EvaluationContext): Any? {
        return if (base == null) {
            //TODO what about stuff like sin() etc
            context.env[name]!!
        } else {
            val left = base.evaluate(context)!!
            return when(val member = Evaluator.from(left).getMember(name)!!) {
                is FieldMember<*> -> member.getCast(left)
                else ->  {
                    try {
                        val actualArgs = args?.map { it.evaluate(context) } ?: listOf()
                        member.apply(left, actualArgs)
                    } catch (r: ReturnException) {
                        return r.value
                    }
                }
            }
        }
    }

    override fun dryRun(context: DryRunContext): Evaluator<*> {
        return if (base == null) {
            context.env[name]!!
        } else {
            val left = base.dryRun(context)
            val member = left.getMember(name)
            if (member == null) {
                throw MemberNotFoundException(left.clazz.java, name)
            } else {
                check(args?.size == member.argTypes.size)
                val actualArgTypes = args?.map { it.dryRun(context) } ?: listOf()
                member.argTypes.zip(actualArgTypes).forEach {
                    check(it.first.accept(it.second))
                }
                member.returnType!!
            }
        }
    }
}