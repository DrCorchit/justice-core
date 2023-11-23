package com.drcorchit.justice.game.events

import com.drcorchit.justice.exceptions.ReturnException
import com.drcorchit.justice.exceptions.ReturnTypeException
import com.drcorchit.justice.game.Game
import com.drcorchit.justice.game.evaluation.DryRunContext
import com.drcorchit.justice.game.evaluation.EvaluationContext
import com.drcorchit.justice.lang.environment.ImmutableTypeEnv
import com.drcorchit.justice.lang.environment.MutableTypeEnv
import com.drcorchit.justice.lang.expression.Expression
import com.drcorchit.justice.lang.statement.Statement
import com.drcorchit.justice.lang.types.Type
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
    override val returnType: Type<*>? by lazy { calculateReturnType() }

    override fun isAuthorized(context: EvaluationContext): Boolean {
        return authorized != null && authorized.evaluate(context) as Boolean
    }

    override fun run(context: EvaluationContext): Any? {
        return try {
            code.execute(context)
        } catch (returned: ReturnException) {
            returned.value
        }
    }

    private fun calculateReturnType(): Type<*>? {
        return try {
            code.dryRun(DryRunContext(parent.parent.types.source, parameters))
        } catch (returned: ReturnTypeException) {
            returned.type
        }
    }

    companion object {
        fun deserialize(game: Game, info: JsonObject): Event {
            val authorized = if (info.has("authorized")) {
                Expression.parse(game.types.source, info["authorized"].asString)
            } else null

            val parameters = MutableTypeEnv()
            parameters.declare("author", StringType, false)
            parameters.declare("timestamp", NumberType, false)
            info.getObject("arguments").entrySet().forEach {
                val type = game.types.source.parseType(it.value.asString)
                parameters.declare(it.key, type, false)
            }

            val code = Statement.parse(game.types.source, info["code"].asString)

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