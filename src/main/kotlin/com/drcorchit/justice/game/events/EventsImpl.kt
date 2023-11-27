package com.drcorchit.justice.game.events

import com.drcorchit.justice.game.Game
import com.drcorchit.justice.game.players.Player
import com.drcorchit.justice.lang.code.Thing
import com.drcorchit.justice.lang.members.LambdaMember
import com.drcorchit.justice.lang.members.Member
import com.drcorchit.justice.lang.types.NonSerializableType
import com.drcorchit.justice.lang.types.Type
import com.drcorchit.justice.utils.Utils.binarySearch
import com.drcorchit.justice.utils.json.Http.Companion.badRequest
import com.drcorchit.justice.utils.json.Http.Companion.internalError
import com.drcorchit.justice.utils.json.Http.Companion.ok
import com.drcorchit.justice.utils.json.HttpResult
import com.drcorchit.justice.utils.json.JsonUtils.toJsonArray
import com.drcorchit.justice.utils.json.info
import com.google.common.collect.ImmutableMap
import com.google.gson.JsonArray
import com.google.gson.JsonObject

class EventsImpl(override val parent: Game) : Events {
    private var events: ImmutableMap<String, Event> = ImmutableMap.of()

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
            val value = event.run(player, info)
            val latency = System.currentTimeMillis() - now
            parent.monitoring.recordLatency(parent, eventID, latency)
            eventHistory.add(EventOutcome(eventID, player, now, latency, info))
            if (value != Thing.UNIT) {
                val resultJson = JsonObject()
                resultJson.add("result", value.serialize())
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
        val output = JsonObject()
        events.values.forEach { output.add(it.name, it.serialize()) }
        output.add("scheduled", scheduled.serialize())
        return output
    }

    override fun deserialize(info: JsonObject) {
        events = ImmutableMap.copyOf(info.getAsJsonArray("events")
            .map { parent.io.loadJson(it.asString) }
            .map { InterpretedEvent.deserialize(parent, it.info.asJsonObject) }
            .associateBy { it.name })
        scheduled.deserialize(info.getAsJsonObject("scheduled"))
    }

    override fun getType(): Type<Events> {
        return evaluator
    }

    private fun createEvaluator(): Type<Events> {
        val builder = ImmutableMap.builder<String, Member<Events>>()
        events.values.forEach {
            val member = LambdaMember(
                Events::class.java,
                it.name,
                it.description,
                it.parameters.toArgs(),
                it.returnType,
                true
            ) { _: Events, args: List<Any?> -> it.run(args.map { arg -> arg!! }) }
            builder.put(member.name, member)
        }

        return object : NonSerializableType<Events>() {
            override val clazz = Events::class.java
            override val members = builder.build()
        }
    }
}