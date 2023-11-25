package com.drcorchit.justice.lang.code.expression

import com.drcorchit.justice.game.evaluation.TypeUniverse
import com.drcorchit.justice.lang.JusticeLexer
import com.drcorchit.justice.lang.JusticeParser
import com.drcorchit.justice.lang.code.Code
import com.drcorchit.justice.lang.code.Visitor
import org.antlr.v4.runtime.CharStreams
import org.antlr.v4.runtime.CommonTokenStream

interface Expression: Code {

    //override fun run(context: EvaluationContext): TypedThing<*>

    //override fun dryRun(context: DryRunContext): Type<*>

    companion object {
        @JvmStatic
        fun parse(types: TypeUniverse, code: String): Expression {
            val lexer = JusticeLexer(CharStreams.fromString(code))
            val tokens = CommonTokenStream(lexer)
            val tree = JusticeParser(tokens)
            return Visitor(types).parse(tree.expression())
        }
    }
}