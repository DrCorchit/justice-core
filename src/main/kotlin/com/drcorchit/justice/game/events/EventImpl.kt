package com.drcorchit.justice.game.events

import com.drcorchit.justice.exceptions.ReturnException
import com.drcorchit.justice.exceptions.ReturnTypeException
import com.drcorchit.justice.game.Game
import com.drcorchit.justice.game.evaluation.ExecutionContext
import com.drcorchit.justice.lang.code.expression.Expression
import com.drcorchit.justice.lang.code.statement.Statement
import com.drcorchit.justice.lang.environment.ImmutableTypeEnv
import com.drcorchit.justice.lang.environment.MutableTypeEnv
import com.drcorchit.justice.lang.types.EventType
import com.drcorchit.justice.lang.code.Thing
import com.drcorchit.justice.lang.types.Type
import com.drcorchit.justice.lang.types.UnitType
import com.drcorchit.justice.lang.types.primitives.NumberType
import com.drcorchit.justice.lang.types.primitives.StringType
import com.drcorchit.justice.utils.Version
import com.drcorchit.justice.utils.json.JsonUtils.getObject
import com.drcorchit.justice.utils.json.JsonUtils.getString
import com.google.gson.JsonObject

data class EventImpl(
    override val parent: Events,
    override val name: String,
    override val version: Version,
    override val description: String,
    override val parameters: ImmutableTypeEnv,
    private val authorized: Expression?,
    private val code: Statement,
) : Event {
    override val uri = parent.uri.extend(name)
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

            return EventImpl(
                game.events,
                info["name"].asString,
                Version(info["version"].asString),
                info.getString("description", "No description available."),
                parameters.immutableCopy(),
                authorized,
                code
            )
        }
    }
}