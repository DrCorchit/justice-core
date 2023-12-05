package com.drcorchit.justice.lang.code.expression

import com.drcorchit.justice.game.evaluation.universe.TypeUniverse
import com.drcorchit.justice.lang.JusticeLexer
import com.drcorchit.justice.lang.JusticeParser
import com.drcorchit.justice.lang.code.Code
import com.drcorchit.justice.lang.code.Visitor
import org.antlr.v4.runtime.CharStreams
import org.antlr.v4.runtime.CommonTokenStream

interface Expression: Code {
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