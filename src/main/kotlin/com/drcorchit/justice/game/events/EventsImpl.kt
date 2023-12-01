package com.drcorchit.justice.game.events

import com.drcorchit.justice.game.Game
import com.drcorchit.justice.game.players.Player
import com.drcorchit.justice.lang.code.Thing
import com.drcorchit.justice.lang.members.Member
import com.drcorchit.justice.lang.members.lambda.LambdaMember
import com.drcorchit.justice.lang.types.NonSerializableType
import com.drcorchit.justice.lang.types.Type
import com.drcorchit.justice.utils.Utils.binarySearch
import com.drcorchit.justice.utils.json.Http.Companion.badRequest
import com.drcorchit.justice.utils.json.Http.Companion.internalError
import com.drcorchit.justice.utils.json.Http.Companion.ok
import com.drcorchit.justice.utils.json.HttpResult
import com.drcorchit.justice.utils.json.JsonUtils.getArray
import com.drcorchit.justice.utils.json.JsonUtils.toJsonArray
import com.drcorchit.justice.utils.json.info
import com.drcorchit.justice.utils.json.toJson
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
            val value = event.trigger(player, info)
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
        val active = events.values.map { it.serialize() }.toJsonArray()
        val scheduled = scheduled.serialize()
        val output = JsonObject()
        output.add("active", active)
        output.add("scheduled", scheduled)
        return output
    }

    override fun deserialize(info: JsonObject) {
        events = info.getAsJsonArray("active")
            .map { parent.io.load(it.asString).toJson() }
            .map { InterpretedEvent.deserialize(parent, it.info.asJsonObject) }
            .associateBy { it.name }
            .let { ImmutableMap.copyOf(it) }
        scheduled.deserialize(info.getArray("scheduled"))
    }

    override fun getType(): Type<Events> {
        return evaluator
    }

    private fun createEvaluator(): Type<Events> {
        return object : NonSerializableType<Events>(Events::class) {
            override val members: ImmutableMap<String, Member<Events>>
            init {
                val builder = ImmutableMap.builder<String, Member<Events>>()
                events.values.forEach {
                    val member = LambdaMember(
                        this,
                        it.name,
                        it.description,
                        it.parameters,
                        it.returnType,
                        true
                    ) { _: Events, args: List<Any> -> it.trigger(args) }
                    builder.put(member.name, member)
                }
                members = builder.build()
            }
        }
    }
}