package com.drcorchit.justice.game.events

import com.drcorchit.justice.game.Game
import com.drcorchit.justice.game.players.PlayerEvaluator
import com.drcorchit.justice.lang.environment.Environment
import com.drcorchit.justice.lang.environment.MutableTypeEnv
import com.drcorchit.justice.lang.environment.TypeEnv
import com.drcorchit.justice.lang.evaluators.Evaluator
import com.drcorchit.justice.lang.evaluators.NumberEvaluator
import com.drcorchit.justice.lang.expression.Expression
import com.drcorchit.justice.lang.statement.ReturnException
import com.drcorchit.justice.lang.statement.ReturnTypeException
import com.drcorchit.justice.lang.statement.Statement
import com.drcorchit.justice.utils.Version
import com.drcorchit.justice.utils.json.JsonUtils.getObject
import com.drcorchit.justice.utils.json.JsonUtils.getString
import com.google.gson.JsonObject

data class EventImpl(
    override val parent: Events,
    override val name: String,
    override val version: Version,
    override val description: String,
    override val parameters: TypeEnv,
    private val authorized: Expression?,
    private val code: Statement,
) : Event {
    override val uri = parent.uri.extend(name)
    override val returnType: Evaluator<*>? by lazy { calculateReturnType() }

    override fun isAuthorized(env: Environment): Boolean {
        return authorized != null && authorized.evaluate(env) as Boolean
    }

    override fun run(env: Environment): Any? {
        return try {
            code.execute(env)
        } catch (returned: ReturnException) {
            returned.value
        }
    }

    private fun calculateReturnType(): Evaluator<*>? {
        return try {
            code.dryRun(parameters)
        } catch (returned: ReturnTypeException) {
            returned.type
        }
    }

    companion object {
        fun deserialize(game: Game, info: JsonObject): Event {
            val authorized = if (info.has("authorized")) {
                Expression.parse(info["authorized"].asString)
            } else null

            val parameters = MutableTypeEnv()
            parameters.declare("author", PlayerEvaluator, false)
            parameters.declare("timestamp", NumberEvaluator, false)
            info.getObject("arguments").entrySet().forEach {
                val type = TODO("Need to implement map from strings to types")
                parameters.declare(it.key, type, false)
            }

            val code = Statement.parse(info["code"].asString)

            return EventImpl(
                game.events,
                info["name"].asString,
                Version(info["version"].asString),
                info.getString("description", "No description available."),
                parameters,
                authorized,
                code
            )
        }
    }
}