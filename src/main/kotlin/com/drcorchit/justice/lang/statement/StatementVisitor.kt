package com.drcorchit.justice.lang.statement

import com.drcorchit.justice.exceptions.CompileException
import com.drcorchit.justice.lang.JusticeBaseVisitor
import com.drcorchit.justice.lang.JusticeParser.*
import com.drcorchit.justice.lang.expression.Expression
import com.drcorchit.justice.lang.expression.ExpressionVisitor
import com.drcorchit.justice.lang.types.source.TypeSource
import com.google.common.collect.ImmutableList

class StatementVisitor(private val universe: TypeSource) : JusticeBaseVisitor<Statement>() {
    val expression = ExpressionVisitor(universe)

    fun parse(stmt: StatementContext): Statement {
        return if (stmt.stmt().size == 1) {
            parse(stmt.stmt().first())
        } else {
            SequenceStatement(ImmutableList.copyOf(stmt.stmt().map { parse(it) }))
        }
    }

    fun parse(stmt: StmtContext): Statement {
        return when (stmt) {
            is DeclareStmtContext -> visitDeclareStmt(stmt)
            is AssignStmtContext -> visitAssignStmt(stmt)
            is IfStmtContext -> visitIfStmt(stmt)
            is ForStmtContext -> visitForStmt(stmt)
            is WhileStmtContext -> visitWhileStmt(stmt)
            is ErrorStmt1Context -> visitErrorStmt1(stmt)
            is ErrorStmt2Context -> visitErrorStmt2(stmt)
            is ReturnStmtContext -> visitReturnStmt(stmt)
            is ExpressionStmtContext -> visitExpressionStmt(stmt)
            else -> throw CompileException("Unsupported statement type: ${stmt.text}")
        }
    }

    override fun visitDeclareStmt(ctx: DeclareStmtContext): Statement {
        val mutable = ctx.VAR().text == "var"
        val id = ctx.ID().text
        val type = if (ctx.typeExpr() == null) null else expression.handleType(ctx.typeExpr())
        val expr = Expression.parse(universe, ctx.expression())
        return DeclareStatement(id, expr, type, mutable)
    }

    override fun visitAssignStmt(ctx: AssignStmtContext): AssignStatement {
        val rhvExpr = expression.parse(ctx.expression())
        return when (val lhv = ctx.lhv()) {
            is LocalAssignContext -> AssignStatement.LocalAssign(rhvExpr, lhv.text)
            is InstanceAssignContext -> {
                val lhvExpr = expression.parse(lhv.expression())
                AssignStatement.InstanceAssign(lhvExpr, rhvExpr, lhv.ID().text)
            }

            is IndexAssignContext -> {
                val lhvExpr = expression.parse(lhv.expression(0))
                val indexExpr = expression.parse(lhv.expression(1))
                AssignStatement.IndexAssign(lhvExpr, rhvExpr, indexExpr)
            }

            else -> throw CompileException("Unknown assignment statement: ${ctx.text}")
        }
    }

    override fun visitIfStmt(ctx: IfStmtContext): Statement {
        val condition = expression.parse(ctx.expression())
        val ifClause = handleBlock(ctx.block(0))
        val elseClause = if (ctx.block().size == 2) {
            handleBlock(ctx.block(1))
        } else null
        return IfStatement(condition, ifClause, elseClause)
    }

    override fun visitForStmt(ctx: ForStmtContext): Statement {
        val id = ctx.ID().text
        val expr = expression.parse(ctx.expression())
        val loop = handleBlock(ctx.block())
        return ForStatement(id, expr, loop)
    }

    override fun visitWhileStmt(ctx: WhileStmtContext): Statement {
        val expr = expression.parse(ctx.expression())
        val loop = handleBlock(ctx.block())
        return WhileStatement(expr, loop)
    }

    override fun visitErrorStmt1(ctx: ErrorStmt1Context): Statement {
        val condition = expression.parse(ctx.expression(0))
        val message = expression.parse(ctx.expression(1))
        return ErrorStatement(condition, message)
    }

    override fun visitErrorStmt2(ctx: ErrorStmt2Context): Statement {
        val message = expression.parse(ctx.expression())
        return ErrorStatement(null, message)
    }

    override fun visitReturnStmt(ctx: ReturnStmtContext): Statement {
        val expr = if (ctx.expression() == null) null else expression.parse(ctx.expression())
        return ReturnStatement(expr)
    }

    override fun visitExpressionStmt(ctx: ExpressionStmtContext): Statement {
        return ExpressionStatement(expression.parse(ctx.expression()))
    }

    private fun handleBlock(ctx: BlockContext): Statement {
        return if (ctx.statement() != null) parse(ctx.statement())
        else if (ctx.stmt() != null) parse(ctx.stmt())
        else throw CompileException("Invalid block: ${ctx.text}")
    }
}