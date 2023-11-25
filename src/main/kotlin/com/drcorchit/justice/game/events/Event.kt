package com.drcorchit.justice.game.events

import com.drcorchit.justice.game.evaluation.ExecutionContext
import com.drcorchit.justice.game.players.Player
import com.drcorchit.justice.lang.environment.ImmutableTypeEnv
import com.drcorchit.justice.lang.types.EventType
import com.drcorchit.justice.lang.code.Thing
import com.drcorchit.justice.lang.types.Type
import com.drcorchit.justice.utils.Version
import com.drcorchit.justice.utils.logging.HasUri
import com.google.gson.JsonObject

interface Event: HasUri {
    override val parent: Events
    val name: String
    val version: Version
    val description: String
    val parameters: ImmutableTypeEnv
    val returnType: Type<*>

    fun isAuthorized(context: ExecutionContext): Boolean

    fun run(author: Player, info: JsonObject): Thing<*> {
        val json = info.deepCopy()
        json.addProperty("author", author.id)
        json.addProperty("timestamp", System.currentTimeMillis())
        val context = parent.parent.types.getExecutionContext(true, Thing(this, EventType))
        context.push(parameters.bind(info, parent.parent, false))
        check(isAuthorized(context))
        return run(context)
    }

    fun run(args: List<Any>): Thing<*> {
        val context = parent.parent.types.getExecutionContext(true, Thing(this, EventType))
        context.push(parameters.bind(args))
        return run(context)
    }

    fun run(context: ExecutionContext): Thing<*>
}