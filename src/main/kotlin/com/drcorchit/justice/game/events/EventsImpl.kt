package com.drcorchit.justice.game.events

import com.drcorchit.justice.game.Game
import com.drcorchit.justice.game.players.Player
import com.drcorchit.justice.lang.evaluators.Evaluator
import com.drcorchit.justice.lang.evaluators.NonSerializableEvaluator
import com.drcorchit.justice.lang.members.LambdaMember
import com.drcorchit.justice.lang.members.Member
import com.drcorchit.justice.utils.Utils.binarySearch
import com.drcorchit.justice.utils.json.Http.Companion.badRequest
import com.drcorchit.justice.utils.json.Http.Companion.internalError
import com.drcorchit.justice.utils.json.Http.Companion.ok
import com.drcorchit.justice.utils.json.HttpResult
import com.drcorchit.justice.utils.json.JsonUtils.toJsonArray
import com.google.common.collect.ImmutableMap
import com.google.gson.JsonArray
import com.google.gson.JsonObject

class EventsImpl(override val parent: Game) : Events {
    private val events = mutableMapOf<String, Event>()

    //Maintains a list of all events from the client.
    private val eventHistory = mutableListOf<EventOutcome>()

    private val evaluator by lazy { createEvaluator() }

    //the date events were last started
    override val scheduled = ScheduledEventsImpl(this)

    override fun post(player: Player, info: JsonObject): HttpResult {
        val now = System.currentTimeMillis()
        val eventID = info["event"].asString
        val event = try {
            events[eventID]!!
        } catch (e: NullPointerException) {
            return badRequest("Event $eventID not found in game ${parent.id}")
        }

        return try {
            val value = event.run(player, now, info)
            val latency = System.currentTimeMillis() - now
            parent.monitoring.recordLatency(parent, eventID, latency)
            eventHistory.add(EventOutcome(eventID, player, now, latency, info))
            if (value != null) {
                val valueJson = Evaluator.from(value).serialize(value)
                val resultJson = JsonObject()
                resultJson.add("result", valueJson)
                ok(resultJson)
            } else {
                ok()
            }
        } catch (e: Exception) {
            eventHistory.add(EventOutcome(eventID, player, now, 0, info, e))
            internalError(e)
        }
    }

    override fun getEventHistory(since: Long): JsonArray {
        return if (since == 0L) eventHistory else {
            val index = eventHistory.binarySearch(since) { it.timestamp }
            eventHistory.subList(index, eventHistory.size - 1)
        }.map { it.info }.toJsonArray()
    }

    override fun serialize(): JsonObject {
        TODO("Not yet implemented")
    }

    override fun deserialize(info: JsonObject) {
        TODO("Not yet implemented")
    }

    override fun getEvaluator(): Evaluator<Events> {
        return evaluator
    }

    private fun createEvaluator(): Evaluator<Events> {
        val builder = ImmutableMap.builder<String, Member<Events>>()
        events.values.forEach {
            val member = LambdaMember<Events>(it.name, it.description, it.parameters.toArgs(), it.returnType) { _, args: List<Any> -> it.run(args) }
            builder.put(member.name, member)
        }

        return object : NonSerializableEvaluator<Events>() {
            override val clazz = Events::class
            override val members = builder.build()
        }
    }
}