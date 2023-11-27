package com.drcorchit.justice.lang.code

import com.drcorchit.justice.exceptions.CompileException
import com.drcorchit.justice.game.evaluation.universe.TypeUniverse
import com.drcorchit.justice.lang.JusticeBaseVisitor
import com.drcorchit.justice.lang.JusticeParser
import com.drcorchit.justice.lang.code.expression.*
import com.drcorchit.justice.lang.code.statement.*
import com.drcorchit.justice.lang.environment.ImmutableTypeEnv
import com.drcorchit.justice.lang.environment.TypeEnvEntry
import com.drcorchit.justice.lang.types.Type
import com.google.common.collect.ImmutableList
import com.google.common.collect.ImmutableMap

class Visitor(val universe: TypeUniverse) : JusticeBaseVisitor<Code>() {

    fun parse(ctx: JusticeParser.StatementContext): Statement {
        return parse(ctx.stmt())
    }

    fun parse(ctx: List<JusticeParser.StmtContext>): Statement {
        return if (ctx.size == 1) parse(ctx.first())
        else SequenceStatement(ImmutableList.copyOf(ctx.map { parse(it) }))
    }

    fun parse(stmt: JusticeParser.StmtContext): Statement {
        return when (stmt) {
            is JusticeParser.DeclareStmtContext -> visitDeclareStmt(stmt)
            is JusticeParser.AssignStmtContext -> visitAssignStmt(stmt)
            is JusticeParser.IfStmtContext -> visitIfStmt(stmt)
            is JusticeParser.ForStmtContext -> visitForStmt(stmt)
            is JusticeParser.WhileStmtContext -> visitWhileStmt(stmt)
            is JusticeParser.ErrorStmt1Context -> visitErrorStmt1(stmt)
            is JusticeParser.ErrorStmt2Context -> visitErrorStmt2(stmt)
            is JusticeParser.ReturnStmtContext -> visitReturnStmt(stmt)
            is JusticeParser.ExpressionStmtContext -> visitExpressionStmt(stmt)
            else -> throw CompileException("Unsupported statement type: ${stmt.text}")
        }
    }

    fun parse(ctx: JusticeParser.ExpressionContext): Expression {
        return when (ctx) {
            is JusticeParser.PowerExprContext -> visitPowerExpr(ctx)
            is JusticeParser.UnaryExprContext -> visitUnaryExpr(ctx)
            is JusticeParser.MultExprContext -> visitMultExpr(ctx)
            is JusticeParser.AddExprContext -> visitAddExpr(ctx)
            is JusticeParser.CompareExprContext -> visitCompareExpr(ctx)
            is JusticeParser.EqualExprContext -> visitEqualExpr(ctx)
            is JusticeParser.AndExprContext -> visitAndExpr(ctx)
            is JusticeParser.OrExprContext -> visitOrExpr(ctx)
            is JusticeParser.ConstExprContext -> visitConstExpr(ctx)
            is JusticeParser.IndexExprContext -> visitIndexExpr(ctx)
            is JusticeParser.LookupContext -> visitLookup(ctx)
            is JusticeParser.LookupEnvContext -> visitLookupEnv(ctx)
            is JusticeParser.ArrayExprContext -> visitArrayExpr(ctx)
            is JusticeParser.LambdaExprContext -> visitLambdaExpr(ctx)
            is JusticeParser.ParenExprContext -> visitParenExpr(ctx)
            else -> throw UnsupportedOperationException("Unsupported expression type: ${ctx.javaClass.simpleName} (${ctx.text})")
        }
    }

    override fun visitDeclareStmt(ctx: JusticeParser.DeclareStmtContext): Statement {
        val mutable = ctx.VAR().text == "var"
        val id = ctx.ID().text
        val type = if (ctx.typeExpr() == null) null else handleType(ctx.typeExpr())
        val expr = parse(ctx.expression())
        return DeclareStatement(id, expr, type, mutable)
    }

    override fun visitAssignStmt(ctx: JusticeParser.AssignStmtContext): AssignStatement {
        val rhvExpr = parse(ctx.expression())
        return when (val lhv = ctx.lhv()) {
            is JusticeParser.LocalAssignContext -> AssignStatement.LocalAssign(rhvExpr, lhv.text)
            is JusticeParser.InstanceAssignContext -> {
                val lhvExpr = parse(lhv.expression())
                AssignStatement.InstanceAssign(lhvExpr, rhvExpr, lhv.ID().text)
            }

            is JusticeParser.IndexAssignContext -> {
                val lhvExpr = parse(lhv.expression(0))
                val indexExpr = parse(lhv.expression(1))
                AssignStatement.IndexAssign(lhvExpr, rhvExpr, indexExpr)
            }

            else -> throw CompileException("Unknown assignment statement: ${ctx.text}")
        }
    }

    override fun visitIfStmt(ctx: JusticeParser.IfStmtContext): Statement {
        val condition = parse(ctx.expression())
        val ifClause = parse(ctx.block(0).stmt())
        val elseClause = if (ctx.block().size == 2) parse(ctx.block(1).stmt())
        else null
        return IfStatement(condition, ifClause, elseClause)
    }

    override fun visitForStmt(ctx: JusticeParser.ForStmtContext): Statement {
        val id = ctx.ID().text
        val expr = parse(ctx.expression())
        val loop = parse(ctx.block().stmt())
        return ForStatement(id, expr, loop)
    }

    override fun visitWhileStmt(ctx: JusticeParser.WhileStmtContext): Statement {
        val expr = parse(ctx.expression())
        val loop = parse(ctx.block().stmt())
        return WhileStatement(expr, loop)
    }

    override fun visitErrorStmt1(ctx: JusticeParser.ErrorStmt1Context): Statement {
        val condition = parse(ctx.expression(0))
        val message = parse(ctx.expression(1))
        return ErrorStatement(condition, message)
    }

    override fun visitErrorStmt2(ctx: JusticeParser.ErrorStmt2Context): Statement {
        val message = parse(ctx.expression())
        return ErrorStatement(null, message)
    }

    override fun visitReturnStmt(ctx: JusticeParser.ReturnStmtContext): Statement {
        val expr = if (ctx.expression() == null) null else parse(ctx.expression())
        return ReturnStatement(expr)
    }

    override fun visitExpressionStmt(ctx: JusticeParser.ExpressionStmtContext): Statement {
        return ExpressionStatement(parse(ctx.expression()))
    }

    override fun visitPowerExpr(ctx: JusticeParser.PowerExprContext): Expression {
        val left = parse(ctx.expression(0))
        val right = parse(ctx.expression(1))
        return BinaryNode(left, right, BinaryNode.Pow)
    }

    override fun visitUnaryExpr(ctx: JusticeParser.UnaryExprContext): Expression {
        val expr = parse(ctx.expression())
        return UnaryNode(expr, UnaryNode.parse(ctx.op.text))
    }

    override fun visitMultExpr(ctx: JusticeParser.MultExprContext): Expression {
        val left = parse(ctx.expression(0))
        val right = parse(ctx.expression(1))
        return BinaryNode(left, right, BinaryNode.parse(ctx.op.text))
    }

    override fun visitAddExpr(ctx: JusticeParser.AddExprContext): Expression {
        val left = parse(ctx.expression(0))
        val right = parse(ctx.expression(1))
        return BinaryNode(left, right, BinaryNode.parse(ctx.op.text))
    }

    override fun visitCompareExpr(ctx: JusticeParser.CompareExprContext): Expression {
        val left = parse(ctx.expression(0))
        val right = parse(ctx.expression(1))
        return BinaryNode(left, right, BinaryNode.parse(ctx.op.text))
    }

    override fun visitEqualExpr(ctx: JusticeParser.EqualExprContext): Expression {
        val left = parse(ctx.expression(0))
        val right = parse(ctx.expression(1))
        return BinaryNode(left, right, BinaryNode.parse(ctx.op.text))
    }

    override fun visitAndExpr(ctx: JusticeParser.AndExprContext): Expression {
        val left = parse(ctx.expression(0))
        val right = parse(ctx.expression(1))
        return BinaryNode(left, right, BinaryNode.And)

    }

    override fun visitOrExpr(ctx: JusticeParser.OrExprContext): Expression {
        val left = parse(ctx.expression(0))
        val right = parse(ctx.expression(1))
        return BinaryNode(left, right, BinaryNode.Or)
    }

    override fun visitConstExpr(ctx: JusticeParser.ConstExprContext): Expression {
        return when (val const = ctx.constant()) {
            is JusticeParser.NullConstContext -> ConstantNode.NULL
            is JusticeParser.BoolConstContext -> if (const.BOOL().text.toBooleanStrict()) ConstantNode.TRUE else ConstantNode.FALSE
            is JusticeParser.IntConstContext -> ConstantNode(const.INT().text.toInt())
            is JusticeParser.LongConstContext -> ConstantNode(const.INT().text.toLong())
            is JusticeParser.RealConstContext -> when (const.REAL().text) {
                "pi" -> ConstantNode.PI
                "e" -> ConstantNode.E
                else -> ConstantNode(const.REAL().text.toDouble())
            }

            is JusticeParser.StrConstContext -> ConstantNode(const.text)
            else -> throw IllegalArgumentException("Unknown constant: ${ctx.text}")
        }
    }

    override fun visitIndexExpr(ctx: JusticeParser.IndexExprContext): Expression {
        return IndexNode(parse(ctx.expression(0)), parse(ctx.expression(1)))
    }

    override fun visitLookup(ctx: JusticeParser.LookupContext): Expression {
        val expr = parse(ctx.expression())
        val name = ctx.ID().text
        val args = handleTuple(ctx.tuple())
        return LookupNode(expr, name, args)
    }

    override fun visitLookupEnv(ctx: JusticeParser.LookupEnvContext): Expression {
        val name = ctx.ID().text
        val args = handleTuple(ctx.tuple())
        return LookupNode(null, name, args)
    }

    override fun visitArrayExpr(ctx: JusticeParser.ArrayExprContext): Expression {
        val typeNode = handleType(ctx.typeExpr())
        val type = typeNode.resolveType(universe)
        return ArrayNode(type, ImmutableList.copyOf(ctx.expression().map { parse(it) }))
    }

    override fun visitLambdaExpr(ctx: JusticeParser.LambdaExprContext): LambdaNode {
        val args = handleArgs(ctx.args())
        val returnExpr = ctx.typeExpr()
        val returnType = if (returnExpr == null) null else handleType(returnExpr).resolveType(universe)
        return handleLambda(args, returnType, ctx.lambdaBody())
    }

    override fun visitParenExpr(ctx: JusticeParser.ParenExprContext): Expression {
        return parse(ctx.expression())
    }

    fun handleLambda(args: ImmutableTypeEnv, returnType: Type<*>?, ctx: JusticeParser.LambdaBodyContext): LambdaNode {
        return when (ctx) {
            is JusticeParser.ExpressionLambdaBodyContext -> LambdaNode(args, returnType, parse(ctx.expression()))
            is JusticeParser.StatementLambdaBodyContext -> LambdaNode(args, returnType, parse(ctx.stmt()))
            else -> throw UnsupportedOperationException("Unsupported lambda type: ${ctx.javaClass.simpleName}")
        }
    }

    fun handleTuple(ctx: JusticeParser.TupleContext?): ImmutableList<Expression> {
        return if (ctx == null) ImmutableList.of()
        else ImmutableList.copyOf(ctx.expression().map { parse(it) })
    }

    fun handleArgs(ctx: JusticeParser.ArgsContext?): ImmutableTypeEnv {
        return if (ctx == null) ImmutableTypeEnv(ImmutableMap.of())
        else {
            val map = ctx.arg().associate { it.ID().text to handleType(it.typeExpr()) }
                .mapValues { TypeEnvEntry(it.key, it.value.resolveType(universe), false) }
            return ImmutableTypeEnv(map)
        }
    }


    fun handleType(ctx: JusticeParser.TypeExprContext): TypeExpression {
        return when (ctx) {
            is JusticeParser.SimpleTypeExprContext -> GenericsTypeNode(ctx.TYPE().text, ImmutableList.of())
            is JusticeParser.ArrayTypeExprContext -> ArrayTypeNode(handleType(ctx.typeExpr()))
            is JusticeParser.GenericsTypeExprContext -> {
                GenericsTypeNode(
                    ctx.TYPE().text,
                    ctx.typeExpr()
                        .map { handleType(it) }
                        .let { ImmutableList.copyOf(it) })
            }

            else -> throw CompileException("Unknown type context: $ctx")
        }
    }
}