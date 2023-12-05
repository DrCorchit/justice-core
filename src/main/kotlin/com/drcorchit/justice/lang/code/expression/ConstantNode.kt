package com.drcorchit.justice.lang.code.expression

import com.drcorchit.justice.exceptions.JusticeException
import com.drcorchit.justice.game.evaluation.context.DryRunContext
import com.drcorchit.justice.game.evaluation.context.ExecutionContext
import com.drcorchit.justice.lang.code.Thing
import com.drcorchit.justice.lang.types.Type
import org.apache.commons.text.StringEscapeUtils

class ConstantNode(val value: Thing<*>) : Expression {
    constructor(input: Any) : this(Thing.wrapPrimitive(input))

    companion object {
        val NULL = ConstantNode(Thing.UNIT)
        val TRUE = ConstantNode(Thing.TRUE)
        val FALSE = ConstantNode(Thing.FALSE)
        val PI = ConstantNode(Thing.PI)
        val E = ConstantNode(Thing.E)
    }

    override fun run(context: ExecutionContext): Thing<*> {
        return value
    }

    override fun dryRun(context: DryRunContext): Type<*> {
        return value.type
    }

    override fun toString(): String {
        return when (val v = value.value) {
            is Boolean -> if (v) "true" else "false"
            is Int -> v.toString()
            is Long -> v.toString() + "L"
            is Double -> if (v == Math.PI) "pi" else if (v == Math.E) "e" else v.toString()
            //Escape strings.
            is String -> "\"" + StringEscapeUtils.escapeJava(v) + "\""
            else -> throw JusticeException("Constant node value <$v> is not a primitive type (Bool, Number, String)")
        }
    }
}