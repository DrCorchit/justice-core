package com.drcorchit.justice.lang.statement

import com.drcorchit.justice.exceptions.ReturnException
import com.drcorchit.justice.exceptions.ReturnTypeException
import com.drcorchit.justice.game.evaluation.DryRunContext
import com.drcorchit.justice.game.evaluation.EvaluationContext
import com.drcorchit.justice.game.evaluation.Types
import com.drcorchit.justice.lang.JusticeLexer
import com.drcorchit.justice.lang.JusticeParser
import com.drcorchit.justice.lang.evaluators.Evaluator
import org.antlr.v4.runtime.CharStreams
import org.antlr.v4.runtime.CommonTokenStream

interface Statement {

    //A statement MAY yield a value when run.
    @Throws(ReturnException::class)
    fun execute(context: EvaluationContext): Any?

    @Throws(ReturnTypeException::class)
    fun dryRun(context: DryRunContext): Evaluator<*>?

    companion object {
        @JvmStatic
        fun parse(types: Types, code: String): Statement {
            val lexer = JusticeLexer(CharStreams.fromString(code))
            val tokens = CommonTokenStream(lexer)
            val tree = JusticeParser(tokens)
            return parse(types, tree.statement())
        }

        fun parse(types: Types, code: JusticeParser.StatementContext): Statement {
            return StatementVisitor(types).parse(code)
        }
    }
}