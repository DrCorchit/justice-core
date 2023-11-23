package com.drcorchit.justice.lang.expression

import com.drcorchit.justice.game.evaluation.DryRunContext
import com.drcorchit.justice.game.evaluation.EvaluationContext
import com.drcorchit.justice.lang.JusticeLexer
import com.drcorchit.justice.lang.JusticeParser
import com.drcorchit.justice.lang.types.Type
import com.drcorchit.justice.lang.types.TypedThing
import com.drcorchit.justice.lang.types.source.TypeSource
import org.antlr.v4.runtime.CharStreams
import org.antlr.v4.runtime.CommonTokenStream

interface Expression {

    fun evaluate(context: EvaluationContext): TypedThing<*>

    fun dryRun(context: DryRunContext): Type<*>

    companion object {
        @JvmStatic
        fun parse(types: TypeSource, code: String): Expression {
            val lexer = JusticeLexer(CharStreams.fromString(code))
            val tokens = CommonTokenStream(lexer)
            val tree = JusticeParser(tokens)
            return parse(types, tree.expression())
        }

        fun parse(types: TypeSource, code: JusticeParser.ExpressionContext): Expression {
            return ExpressionVisitor(types).parse(code)
        }
    }
}