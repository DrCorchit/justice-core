package com.drcorchit.justice.game.events

import com.drcorchit.justice.exceptions.ReturnException
import com.drcorchit.justice.exceptions.ReturnTypeException
import com.drcorchit.justice.game.Game
import com.drcorchit.justice.game.evaluation.context.ExecutionContext
import com.drcorchit.justice.lang.JusticeLexer
import com.drcorchit.justice.lang.JusticeParser
import com.drcorchit.justice.lang.code.Thing
import com.drcorchit.justice.lang.code.Visitor
import com.drcorchit.justice.lang.code.expression.Expression
import com.drcorchit.justice.lang.code.statement.Statement
import com.drcorchit.justice.lang.environment.ImmutableTypeEnv
import com.drcorchit.justice.lang.environment.MutableTypeEnv
import com.drcorchit.justice.lang.environment.TypeEnvEntry
import com.drcorchit.justice.lang.types.EventType
import com.drcorchit.justice.lang.types.Type
import com.drcorchit.justice.lang.types.UnitType
import com.drcorchit.justice.lang.types.primitives.NumberType
import com.drcorchit.justice.lang.types.primitives.StringType
import com.drcorchit.justice.utils.Version
import com.drcorchit.justice.utils.json.JsonUtils.getObject
import com.drcorchit.justice.utils.json.JsonUtils.getString
import com.google.gson.JsonElement
import com.google.gson.JsonObject
import org.antlr.v4.runtime.CharStreams
import org.antlr.v4.runtime.CommonTokenStream

class InterpretedEvent(
    parent: Events,
    name: String,
    description: String,
    version: Version,
    parameters: ImmutableTypeEnv,
    private val authorized: Expression?,
    private val code: Statement,
) : AbstractEvent(parent, name, description, version, parameters) {
    override val returnType: Type<*> by lazy { calculateReturnType() }

    override fun isAuthorized(context: ExecutionContext): Boolean {
        return authorized != null && authorized.run(context).value as Boolean
    }

    override fun run(context: ExecutionContext): Thing<*> {
        return try {
            code.run(context)
        } catch (returned: ReturnException) {
            returned.value
        }
    }

    private fun calculateReturnType(): Type<*> {
        return try {
            code.dryRun(parent.parent.types.getDryRunContext(true, EventType))
            UnitType
        } catch (returned: ReturnTypeException) {
            returned.type
        }
    }

    override fun serialize(): JsonElement {
        //TODO
        val output = JsonObject()
        return output
    }

    companion object {
        fun deserialize(game: Game, info: JsonObject): Event {
            val authorized = if (info.has("authorized")) {
                Expression.parse(game.types.universe, info["authorized"].asString)
            } else null

            val parameters = MutableTypeEnv()
            parameters.declare("author", StringType, false)
            parameters.declare("timestamp", NumberType, false)
            info.getObject("arguments").entrySet().forEach {
                val type = game.types.universe.parseType(it.value.asString)
                parameters.declare(it.key, type, false)
            }

            val code = Statement.parse(game.types.universe, info["code"].asString)

            return InterpretedEvent(
                game.events,
                info["name"].asString,
                info.getString("description", "No description available."),
                Version(info["version"].asString),
                parameters.immutableCopy(),
                authorized,
                code
            )
        }

        fun parse(game: Game, code: String): InterpretedEvent {
            val lexer = JusticeLexer(CharStreams.fromString(code))
            val tokens = CommonTokenStream(lexer)
            val tree = JusticeParser(tokens)
            val ctx = tree.event()
            val metadata = ctx.eventMetadata()
            val parameters = ctx.eventParameters()
            val visitor = Visitor(game.types.universe)
            val parsedParams = parameters.eventParameter()
                .map {
                    TypeEnvEntry(
                        it.ID().text,
                        visitor.handleType(it.typeExpr()).resolveType(game.types.universe),
                        false
                    )
                }
                .associateBy { it.id }
                .let { ImmutableTypeEnv(it) }
            val authorized = ctx.eventAuthorization()?.let { visitor.parse(it.expression()) }
            val eventCode = visitor.parse(ctx.eventCode().stmt())

            return InterpretedEvent(
                game.events,
                metadata.ID().text,
                metadata.STR().text ?: "No description available.",
                Version(metadata.VERSION().text),
                parsedParams,
                authorized,
                eventCode
            )
        }
    }
}