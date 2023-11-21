package com.drcorchit.justice.game.evaluation

import com.drcorchit.justice.exceptions.UnsupportedTypeException
import com.drcorchit.justice.game.Game
import com.drcorchit.justice.lang.environment.Environment
import com.drcorchit.justice.lang.environment.MutableEnvironment
import com.drcorchit.justice.lang.evaluators.Evaluator
import com.drcorchit.justice.lang.evaluators.HasEvaluator
import com.drcorchit.justice.lang.expression.Expression
import com.drcorchit.justice.lang.statement.Statement
import com.drcorchit.justice.utils.Utils.createCache
import com.drcorchit.justice.utils.json.Result
import com.google.gson.JsonNull
import com.google.gson.JsonObject

class JusticeTypes(override val parent: Game) : Types {
    val cache = createCache<Any, Evaluator<*>>(1000) {
        if (it is HasEvaluator<*>) {
            it.getEvaluator()
        }
        throw UnsupportedTypeException(it::class.java)
    }

    override val baseEnv: Environment
        get() {
            val output = MutableEnvironment()
            //Adding entries equates to adding global variables visible in all contexts!
            output.declare("players", parent.players.getEvaluator(), parent.players, false)
            output.declare("mechanics", parent.mechanics.getEvaluator(), parent.mechanics, false)
            output.declare("events", parent.events.getEvaluator(), parent.events, false)
            output.declare("metadata", parent.metadata.getEvaluator(), parent.metadata, false)
            return output
        }

    override fun query(query: Expression): Result {
        val context = EvaluationContext(parent, baseEnv, false)
        //TODO Avoid this type of cast.
        val type = query.dryRun(context.toDryRunContext()) as Evaluator<Any>
        val result = query.evaluate(context)
        val json = if (result == null) JsonNull.INSTANCE else type.serialize(result)
        val info = JsonObject()
        info.add("result", json)
        return Result.succeedWithInfo(info)
    }

    override fun execute(command: Statement): Result {
        val context = EvaluationContext(parent, baseEnv, true)
        val type = command.dryRun(context.toDryRunContext())
        val result = command.execute(context)
        return if (type == null || result == null) {
            Result.succeed()
        } else {
            //TODO Avoid this type of cast.
            val json = (type as Evaluator<Any>).serialize(result)
            val info = JsonObject()
            info.add("result", json)
            Result.succeedWithInfo(info)
        }
    }

    override fun getType(name: String): Evaluator<*>? {
        TODO("Not yet implemented")
    }

    override fun getType(instance: Any): Evaluator<*>? {
        TODO("Not yet implemented")
    }


}