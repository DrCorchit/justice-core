package com.drcorchit.justice.lang.statement

import com.drcorchit.justice.exceptions.CompileException
import com.drcorchit.justice.game.evaluation.Types
import com.drcorchit.justice.lang.JusticeBaseVisitor
import com.drcorchit.justice.lang.JusticeParser.AssignContext
import com.drcorchit.justice.lang.JusticeParser.DeclareContext
import com.drcorchit.justice.lang.JusticeParser.ElseBranchContext
import com.drcorchit.justice.lang.JusticeParser.ErrorContext
import com.drcorchit.justice.lang.JusticeParser.ExpressionStmtContext
import com.drcorchit.justice.lang.JusticeParser.ForLoopContext
import com.drcorchit.justice.lang.JusticeParser.IfBranchContext
import com.drcorchit.justice.lang.JusticeParser.ReturnStmtContext
import com.drcorchit.justice.lang.JusticeParser.StatementContext
import com.drcorchit.justice.lang.JusticeParser.StmtContext
import com.drcorchit.justice.lang.JusticeParser.WhileLoopContext
import com.google.common.collect.ImmutableList

class StatementVisitor(universe: Types) : JusticeBaseVisitor<Statement>() {

    fun parse(stmt: StatementContext): Statement {
        return if (stmt.stmt().size == 1) {
            parse(stmt.stmt().first())
        } else {
            SequenceStatement(ImmutableList.copyOf(stmt.stmt().map { parse(it) }))
        }
    }

    fun parse(stmt: StmtContext): Statement {
        return when (stmt) {
            is DeclareContext -> visitDeclare(stmt)
            is AssignContext -> visitAssign(stmt)
            is IfBranchContext -> visitIfBranch(stmt)
            is ElseBranchContext -> visitElseBranch(stmt)
            is ForLoopContext -> visitForLoop(stmt)
            is WhileLoopContext -> visitWhileLoop(stmt)
            is ErrorContext -> visitError(stmt)
            is ReturnStmtContext -> visitReturnStmt(stmt)
            is ExpressionStmtContext -> visitExpressionStmt(stmt)
            else -> throw CompileException("Unsupported statement type: $stmt")
        }
    }

    override fun visitDeclare(ctx: DeclareContext): Statement {

    }
}