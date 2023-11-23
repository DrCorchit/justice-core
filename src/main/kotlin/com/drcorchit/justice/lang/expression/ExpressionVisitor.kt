package com.drcorchit.justice.lang.expression

import com.drcorchit.justice.exceptions.CompileException
import com.drcorchit.justice.lang.JusticeBaseVisitor
import com.drcorchit.justice.lang.JusticeParser.*
import com.drcorchit.justice.lang.environment.ImmutableTypeEnv
import com.drcorchit.justice.lang.environment.TypeEnvEntry
import com.drcorchit.justice.lang.statement.Statement
import com.drcorchit.justice.lang.types.Type
import com.drcorchit.justice.lang.types.source.TypeSource
import com.google.common.collect.ImmutableList
import com.google.common.collect.ImmutableMap

class ExpressionVisitor(private val universe: TypeSource) : JusticeBaseVisitor<Expression>() {

    fun parse(ctx: ExpressionContext): Expression {
        return when (ctx) {
            is PowerExprContext -> visitPowerExpr(ctx)
            is UnaryExprContext -> visitUnaryExpr(ctx)
            is MultExprContext -> visitMultExpr(ctx)
            is AddExprContext -> visitAddExpr(ctx)
            is CompareExprContext -> visitCompareExpr(ctx)
            is EqualExprContext -> visitEqualExpr(ctx)
            is AndExprContext -> visitAndExpr(ctx)
            is OrExprContext -> visitOrExpr(ctx)
            is ConstExprContext -> visitConstExpr(ctx)
            is IndexExprContext -> visitIndexExpr(ctx)
            is LookupContext -> visitLookup(ctx)
            is LookupEnvContext -> visitLookupEnv(ctx)
            is ArrayExprContext -> visitArrayExpr(ctx)
            //is TupleExprContext -> visitTupleExpr(ctx)
            is LambdaExprContext -> visitLambdaExpr(ctx)
            is ParenExprContext -> visitParenExpr(ctx)
            else -> throw UnsupportedOperationException("Unsupported expression type: ${ctx.javaClass.simpleName} (${ctx.text})")
        }
    }

    override fun visitPowerExpr(ctx: PowerExprContext): Expression {
        val left = parse(ctx.expression(0))
        val right = parse(ctx.expression(1))
        return BinaryNode(left, right, BinaryNode.Pow)
    }

    override fun visitUnaryExpr(ctx: UnaryExprContext): Expression {
        val expr = parse(ctx.expression())
        return UnaryNode(expr, UnaryNode.parse(ctx.op.text))
    }

    override fun visitMultExpr(ctx: MultExprContext): Expression {
        val left = parse(ctx.expression(0))
        val right = parse(ctx.expression(1))
        return BinaryNode(left, right, BinaryNode.parse(ctx.op.text))
    }

    override fun visitAddExpr(ctx: AddExprContext): Expression {
        val left = parse(ctx.expression(0))
        val right = parse(ctx.expression(1))
        return BinaryNode(left, right, BinaryNode.parse(ctx.op.text))
    }

    override fun visitCompareExpr(ctx: CompareExprContext): Expression {
        val left = parse(ctx.expression(0))
        val right = parse(ctx.expression(1))
        return BinaryNode(left, right, BinaryNode.parse(ctx.op.text))
    }

    override fun visitEqualExpr(ctx: EqualExprContext): Expression {
        val left = parse(ctx.expression(0))
        val right = parse(ctx.expression(1))
        return BinaryNode(left, right, BinaryNode.parse(ctx.op.text))
    }

    override fun visitAndExpr(ctx: AndExprContext): Expression {
        val left = parse(ctx.expression(0))
        val right = parse(ctx.expression(1))
        return BinaryNode(left, right, BinaryNode.And)

    }

    override fun visitOrExpr(ctx: OrExprContext): Expression {
        val left = parse(ctx.expression(0))
        val right = parse(ctx.expression(1))
        return BinaryNode(left, right, BinaryNode.Or)
    }

    override fun visitConstExpr(ctx: ConstExprContext): Expression {
        return when (val const = ctx.constant()) {
            is NullConstContext -> throw UnsupportedOperationException("Null literal is not currently supported.")
            is BoolConstContext -> if (const.BOOL().text.toBooleanStrict()) ConstantNode.TRUE else ConstantNode.FALSE
            is IntConstContext -> ConstantNode(const.INT().text.toInt())
            is LongConstContext -> ConstantNode(const.INT().text.toLong())
            is RealConstContext -> when (const.REAL().text) {
                "pi" -> ConstantNode.PI
                "e" -> ConstantNode.E
                else -> ConstantNode(const.REAL().text.toDouble())
            }

            is StrConstContext -> ConstantNode(const.text)
            else -> throw IllegalArgumentException("Unknown constant: ${ctx.text}")
        }
    }

    override fun visitIndexExpr(ctx: IndexExprContext): Expression {
        return IndexNode(parse(ctx.expression(0)), parse(ctx.expression(1)))
    }

    override fun visitLookup(ctx: LookupContext): Expression {
        val expr = parse(ctx.expression())
        val name = ctx.ID().text
        val args = handleTuple(ctx.tuple())
        return LookupNode(expr, name, args)
    }

    override fun visitLookupEnv(ctx: LookupEnvContext): Expression {
        val name = ctx.ID().text
        val args = handleTuple(ctx.tuple())
        return LookupNode(null, name, args)
    }

    override fun visitArrayExpr(ctx: ArrayExprContext): Expression {
        val typeNode = TypeNode(ctx.TYPE().text)
        val type = typeNode.evaluateType(universe) as Type<*>
        return ArrayNode(type, ImmutableList.copyOf(ctx.expression().map { parse(it) }))
    }

    override fun visitLambdaExpr(ctx: LambdaExprContext): LambdaNode {
        val args = handleArgs(ctx.args())
        val returnExpr = ctx.typeExpr()
        val returnType = if (returnExpr == null) null else handleType(returnExpr).evaluateType(universe)
        return handleLambda(args, returnType, ctx.lambdaBody())
    }

    override fun visitParenExpr(ctx: ParenExprContext): Expression {
        return parse(ctx.expression())
    }

    fun handleLambda(args: ImmutableTypeEnv, returnType: Type<*>?, ctx: LambdaBodyContext): LambdaNode {
        return when (ctx) {
            is ExpressionLambdaBodyContext -> LambdaNode.ExpressionLambdaNode(
                args, returnType, parse(ctx.expression()))
            is StatementLambdaBodyContext -> LambdaNode.StatementLambdaNode(
                args, returnType, Statement.parse(universe, ctx.statement())
            )
            else -> throw UnsupportedOperationException("Unsupported lambda type: ${ctx.javaClass.simpleName}")
        }
    }

    fun handleTuple(ctx: TupleContext?): ImmutableList<Expression> {
        return if (ctx == null) ImmutableList.of()
        else ImmutableList.copyOf(ctx.expression().map { parse(it) })
    }

    fun handleArgs(ctx: ArgsContext?): ImmutableTypeEnv {
        return if (ctx == null) ImmutableTypeEnv(ImmutableMap.of(), null)
        else {
            val map = ctx.arg().associate { it.ID().text to handleType(it.typeExpr()) }
            return ImmutableTypeEnv(
                map.mapValues { TypeEnvEntry(it.key, it.value.evaluateType(universe)!!, false) }, null
            )
        }
    }

    fun handleType(ctx: TypeExprContext): TypeExpression {
        return when (ctx) {
            is BaseTypeExprContext -> TypeNode(ctx.TYPE().text)
            is ArrayTypeExprContext -> ArrayTypeNode(handleType(ctx.typeExpr()))
            else -> throw CompileException("Unknown type context: $ctx")
        }
    }
}