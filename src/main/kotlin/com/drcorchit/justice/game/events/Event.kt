package com.drcorchit.justice.game.events

import com.drcorchit.justice.game.players.Player
import com.drcorchit.justice.lang.environment.Environment
import com.drcorchit.justice.lang.environment.TypeEnv
import com.drcorchit.justice.lang.evaluators.Evaluator
import com.drcorchit.justice.utils.Version
import com.drcorchit.justice.utils.logging.HasUri
import com.google.gson.JsonObject

interface Event: HasUri {
    override val parent: Events
    val name: String
    val version: Version
    val description: String
    val parameters: TypeEnv
    val returnType: Evaluator<*>?

    fun isAuthorized(env: Environment): Boolean

    fun run(author: Player, timestamp: Long, info: JsonObject): Any? {
        val json = info.deepCopy()
        json.addProperty("author", author.id)
        json.addProperty("timestamp", timestamp)
        val env = parameters.bind(info, parent.parent, null, false)
        check(isAuthorized(env))
        return run(env)
    }

    fun run(args: List<Any>): Any? {
        return run(parameters.bind(args))
    }

    fun run(env: Environment): Any?
}