package com.drcorchit.justice.game.evaluation

import com.drcorchit.justice.game.Game
import com.drcorchit.justice.game.events.Events
import com.drcorchit.justice.game.mechanics.GameElement
import com.drcorchit.justice.game.mechanics.GameMechanic
import com.drcorchit.justice.game.mechanics.Mechanics
import com.drcorchit.justice.lang.environment.Environment
import com.drcorchit.justice.lang.environment.MutableEnvironment
import com.drcorchit.justice.lang.expression.Expression
import com.drcorchit.justice.lang.statement.Statement
import com.drcorchit.justice.lang.types.ElementType
import com.drcorchit.justice.lang.types.MechanicType
import com.drcorchit.justice.lang.types.source.ImmutableTypeSource
import com.drcorchit.justice.lang.types.source.TypeSource
import com.drcorchit.justice.utils.json.Result
import com.google.gson.JsonNull
import com.google.gson.JsonObject
import kotlin.reflect.KClass

class JusticeTypes(override val parent: Game) : Types {
    override val source: ImmutableTypeSource

    init {
        val builder = TypeSource.universe.mutableCopy()
        builder.registerType(Mechanics::class) { _ -> parent.mechanics.getType() }
        builder.registerType(Events::class) { parent.events.getType() }
        builder.registerType(GameMechanic::class) { MechanicType(builder, it as KClass<out GameMechanic<*>>) }
        builder.registerType(GameElement::class) { ElementType(builder, it as KClass<out GameElement>) }
        source = builder.immutableCopy()
    }

    override val baseEnv: Environment
        get() {
            val output = MutableEnvironment()
            //Adding entries equates to adding global variables visible in all contexts!
            output.declare("players", parent.players.getType(), parent.players, false)
            output.declare("mechanics", parent.mechanics.getType(), parent.mechanics, false)
            output.declare("events", parent.events.getType(), parent.events, false)
            output.declare("metadata", parent.metadata.getType(), parent.metadata, false)
            return output
        }

    override fun query(query: String): Result {
        val expr = Expression.parse(source, query)
        val context = EvaluationContext(parent, baseEnv, false)
        val type = expr.dryRun(context.toDryRunContext())
        val result = expr.evaluate(context)
        val json = if (result == null) JsonNull.INSTANCE else type.serializeCast(result)
        val info = JsonObject()
        info.add("result", json)
        return Result.succeedWithInfo(info)
    }

    override fun execute(command: String): Result {
        val stmt = Statement.parse(source, command)
        val context = EvaluationContext(parent, baseEnv, true)
        val type = stmt.dryRun(context.toDryRunContext())
        val result = stmt.execute(context)
        return if (type == null || result == null) {
            Result.succeed()
        } else {
            val json = type.serializeCast(result)
            val info = JsonObject()
            info.add("result", json)
            Result.succeedWithInfo(info)
        }
    }
}