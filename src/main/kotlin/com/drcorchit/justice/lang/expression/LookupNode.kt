package com.drcorchit.justice.lang.expression

import com.drcorchit.justice.exceptions.MemberNotFoundException
import com.drcorchit.justice.game.evaluation.DryRunContext
import com.drcorchit.justice.game.evaluation.EvaluationContext
import com.drcorchit.justice.lang.evaluators.Evaluator
import com.google.common.collect.ImmutableList

class LookupNode(
    private val base: Expression?,
    private val name: String,
    //null args means no parens
    //empty args means empty parens ()
    private val args: ImmutableList<Expression>?
) : Expression {
    override fun evaluate(context: EvaluationContext): Any {
        return if (base == null) {
            context.env[name]!!
        } else {
            val left = base.evaluate(context)
            val member = Evaluator.from(left).getMember(name)
            val actualArgs = args?.map { it.evaluate(context) } ?: listOf()
            member?.apply(left, actualArgs) ?: throw MemberNotFoundException(left::class.java, name)
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