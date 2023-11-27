package com.drcorchit.justice.lang.code.statement

import com.drcorchit.justice.exceptions.TypeException
import com.drcorchit.justice.game.evaluation.context.DryRunContext
import com.drcorchit.justice.game.evaluation.context.ExecutionContext
import com.drcorchit.justice.lang.code.Thing
import com.drcorchit.justice.lang.code.expression.Expression
import com.drcorchit.justice.lang.types.AnyType
import com.drcorchit.justice.lang.types.IterableType
import com.drcorchit.justice.lang.types.Type
import com.drcorchit.justice.lang.types.UnitType

class ForStatement(val id: String, val iter: Expression, val loop: Statement): Statement {
    var type: IterableType? = null

    override fun run(context: ExecutionContext): Thing<*> {
        context.push()
        context.declare(id, type!!.itemType, null, true)

        val iterable = iter.run(context).value as Iterable<*>
        iterable.forEach {
            context.assign(id, it!!)
            loop.run(context)
        }
        context.pop()
        return Thing.UNIT
    }

    override fun dryRun(context: DryRunContext): Type<*> {
        val actualType = iter.dryRun(context)
        if (actualType is IterableType) {
            type = actualType
        } else {
            throw TypeException("for", IterableType(AnyType), actualType)
        }
        context.push()
        context.assign(id, type!!.itemType)
        loop.dryRun(context)
        context.pop()
        return UnitType
    }

    override fun toString(): String {
        return "for ($iter) {\n$loop\n}"
    }
}