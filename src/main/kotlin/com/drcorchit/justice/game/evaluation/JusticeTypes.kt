package com.drcorchit.justice.game.evaluation

import com.drcorchit.justice.game.Game
import com.drcorchit.justice.game.evaluation.context.DryRunContext
import com.drcorchit.justice.game.evaluation.context.ExecutionContext
import com.drcorchit.justice.game.evaluation.context.StackDryRunContext
import com.drcorchit.justice.game.evaluation.context.StackExecutionContext
import com.drcorchit.justice.game.evaluation.instantiators.ElementTypeInstantiator
import com.drcorchit.justice.game.evaluation.instantiators.MechanicTypeInstantiator
import com.drcorchit.justice.game.evaluation.instantiators.SimpleTypeInstantiator
import com.drcorchit.justice.game.evaluation.universe.TypeUniverse
import com.drcorchit.justice.game.metadata.MetadataType
import com.drcorchit.justice.game.players.PlayersType
import com.drcorchit.justice.lang.code.Thing
import com.drcorchit.justice.lang.code.expression.Expression
import com.drcorchit.justice.lang.code.statement.Statement
import com.drcorchit.justice.lang.types.Type
import com.drcorchit.justice.utils.json.Result
import com.google.gson.JsonObject

class JusticeTypes(override val parent: Game) : Types {
    override val universe: TypeUniverse

    init {
        val builder = TypeUniverse.getDefault()
        builder.registerType(PlayersType)
        builder.registerType(SimpleTypeInstantiator(parent.mechanics.getType()))
        builder.registerType(SimpleTypeInstantiator(parent.events.getType()))
        builder.registerType(MetadataType)
        builder.registerType(MechanicTypeInstantiator(builder))
        builder.registerType(ElementTypeInstantiator(builder))
        universe = builder
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