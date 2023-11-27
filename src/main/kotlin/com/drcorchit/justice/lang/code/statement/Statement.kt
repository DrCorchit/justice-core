package com.drcorchit.justice.lang.code.statement

import com.drcorchit.justice.lang.JusticeLexer
import com.drcorchit.justice.lang.JusticeParser
import com.drcorchit.justice.lang.code.Code
import com.drcorchit.justice.lang.code.Visitor
import com.drcorchit.justice.game.evaluation.universe.TypeUniverse
import org.antlr.v4.runtime.CharStreams
import org.antlr.v4.runtime.CommonTokenStream

interface Statement: Code {

    //@Throws(ReturnException::class)
    //fun run(context: EvaluationContext): TypedThing<*>

    //@Throws(ReturnTypeException::class)
    //fun dryRun(context: DryRunContext): Type<*>

    companion object {
        @JvmStatic
        fun parse(types: TypeUniverse, code: String): Statement {
            val lexer = JusticeLexer(CharStreams.fromString(code))
            val tokens = CommonTokenStream(lexer)
            val tree = JusticeParser(tokens)
            return Visitor(types).parse(tree.statement())
        }
    }
}