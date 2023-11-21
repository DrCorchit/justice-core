package com.drcorchit.justice.lang.expression

import com.drcorchit.justice.game.evaluation.DryRunContext
import com.drcorchit.justice.game.evaluation.EvaluationContext
import com.drcorchit.justice.game.evaluation.Types
import com.drcorchit.justice.lang.JusticeLexer
import com.drcorchit.justice.lang.JusticeParser
import com.drcorchit.justice.lang.evaluators.Evaluator
import com.drorchit.justice.parsing.expression.ExpressionVisitor
import org.antlr.v4.runtime.CharStreams
import org.antlr.v4.runtime.CommonTokenStream

interface Expression {

    fun evaluate(context: EvaluationContext): Any?

    fun dryRun(context: DryRunContext): Evaluator<*>

    companion object {
        @JvmStatic
        fun parse(types: Types, code: String): Expression {
            val lexer = JusticeLexer(CharStreams.fromString(code))
            val tokens = CommonTokenStream(lexer)
            val tree = JusticeParser(tokens)
            return parse(types, tree.expression())
        }

        fun parse(types: Types, code: JusticeParser.ExpressionContext): Expression {
            return ExpressionVisitor(types).parse(code)
        }
    }
}