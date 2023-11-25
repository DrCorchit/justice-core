package com.drcorchit.justice.game.evaluation

import com.drcorchit.justice.game.Game
import com.drcorchit.justice.game.events.Events
import com.drcorchit.justice.game.mechanics.GameElement
import com.drcorchit.justice.game.mechanics.GameMechanic
import com.drcorchit.justice.game.mechanics.Mechanics
import com.drcorchit.justice.game.metadata.MetadataType
import com.drcorchit.justice.game.players.PlayersType
import com.drcorchit.justice.lang.code.expression.Expression
import com.drcorchit.justice.lang.code.statement.Statement
import com.drcorchit.justice.lang.types.ElementType
import com.drcorchit.justice.lang.types.MechanicType
import com.drcorchit.justice.lang.types.Thing
import com.drcorchit.justice.lang.types.Type
import com.drcorchit.justice.utils.json.Result
import com.google.gson.JsonObject
import kotlin.reflect.KClass

class JusticeTypes(override val parent: Game) : Types {
    override val universe: ImmutableTypeUniverse

    init {
        val builder = TypeUniverse.getDefault().mutableCopy()
        builder.registerType(Mechanics::class) { parent.mechanics.getType() }
        builder.registerType(Events::class) { parent.events.getType() }
        builder.registerType(GameMechanic::class) { MechanicType(it.kClass as KClass<out GameMechanic<*>>, builder) }
        builder.registerType(GameElement::class) { ElementType(it.kClass as KClass<out GameElement>, builder) }
        universe = builder.immutableCopy()
    }

    override fun query(query: String): Result {
        return try {
            val expr = Expression.parse(universe, query)
            expr.dryRun(getDryRunContext(false, null))
            val result = expr.run(getExecutionContext(false, null))
            val info = JsonObject()
            info.add("result", result.serialize())
            Result.succeedWithInfo(info)
        } catch (e: Exception) {
            Result.failWithError(e)
        }
    }

    override fun execute(command: String): Result {
        return try {
            val stmt = Statement.parse(universe, command)
            stmt.dryRun(getDryRunContext(true, null))
            val result = stmt.run(getExecutionContext(true, null))
            val info = JsonObject()
            info.add("result", result.serialize())
            Result.succeedWithInfo(info)
        } catch (e: Exception) {
            Result.failWithError(e)
        }
    }

    override fun getExecutionContext(allowSideEffects: Boolean, self: Thing<*>?): ExecutionContext {
        val output = StackExecutionContext(universe, allowSideEffects, self)
        //Adding entries equates to adding global variables visible in all contexts!
        output.declareGlobal("players", parent.players.asThing)
        output.declareGlobal("mechanics", parent.mechanics.asThing)
        output.declareGlobal("events", parent.events.asThing)
        output.declareGlobal("metadata", parent.metadata.asThing)
        return output
    }

    override fun getDryRunContext(allowSideEffects: Boolean, self: Type<*>?): DryRunContext {
        val output = StackDryRunContext(universe, allowSideEffects, self)
        output.declareGlobal("players", PlayersType)
        output.declareGlobal("mechanics", parent.mechanics.getType())
        output.declareGlobal("events", parent.events.getType())
        output.declareGlobal("metadata", MetadataType)
        return output
    }
}