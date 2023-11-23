package com.drcorchit.justice.lang.statement

import com.drcorchit.justice.game.evaluation.DryRunContext
import com.drcorchit.justice.game.evaluation.EvaluationContext
import com.drcorchit.justice.lang.environment.MutableEnvironment
import com.drcorchit.justice.lang.types.AnyType
import com.drcorchit.justice.lang.types.Type
import com.drcorchit.justice.lang.expression.Expression

class ForStatement(val id: String, val iter: Expression, val loop: Statement): Statement {
    override fun execute(context: EvaluationContext): Any? {
        val env = MutableEnvironment(context.env)
        val iterable = iter.evaluate(context) as Iterable<*>
        //TODO is there a way to find this value's type?
        env.declare(id, AnyType, null, true)
        iterable.forEach {
            env.assign(id, it!!)
            loop.execute(EvaluationContext(context.game, env, context.allowMutation))
        }
        return null
    }

    override fun dryRun(context: DryRunContext): Type<*>? {
        //TODO improve this
        iter.dryRun(context)
        loop.dryRun(context)
        return null
    }
}