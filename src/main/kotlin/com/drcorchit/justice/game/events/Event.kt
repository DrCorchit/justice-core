package com.drcorchit.justice.game.events

import com.drcorchit.justice.game.evaluation.EvaluationContext
import com.drcorchit.justice.game.players.Player
import com.drcorchit.justice.lang.environment.ImmutableTypeEnv
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
    val returnType: Type<*>?

    fun isAuthorized(context: EvaluationContext): Boolean

    fun run(author: Player, timestamp: Long, info: JsonObject): Any? {
        val json = info.deepCopy()
        json.addProperty("author", author.id)
        json.addProperty("timestamp", timestamp)
        val env = parameters.bind(info, parent.parent, null, false)
        val context = EvaluationContext(parent.parent, env, true)
        check(isAuthorized(context))
        return run(context)
    }

    fun run(args: List<Any>): Any? {
        val env = parameters.bind(args)
        return run(EvaluationContext(parent.parent, env, true))
    }

    fun run(context: EvaluationContext): Any?
}