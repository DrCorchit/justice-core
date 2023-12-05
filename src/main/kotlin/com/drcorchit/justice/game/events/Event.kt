package com.drcorchit.justice.game.events

import com.drcorchit.justice.exceptions.MemberNotFoundException
import com.drcorchit.justice.game.evaluation.environment.Parameters
import com.drcorchit.justice.lang.annotations.HardcodedEvent
import com.drcorchit.justice.lang.code.Thing
import com.drcorchit.justice.lang.members.reflection.ReflectionMember
import com.drcorchit.justice.lang.types.Type
import com.drcorchit.justice.utils.Version
import com.drcorchit.justice.utils.logging.HasUri
import com.google.gson.*
import kotlin.reflect.KCallable
import kotlin.reflect.KClass
import kotlin.reflect.full.primaryConstructor
import kotlin.reflect.full.valueParameters

interface Event : HasUri {
    override val parent: Events
    val name: String
    val version: Version
    val description: String
    val parameters: Parameters
    val returnType: Type<*>

    fun isAuthorized(args: List<Any>): Boolean

    fun trigger(info: JsonObject): Thing<*> {
        val args = parameters.deserialize(info, parent.parent)
        check(isAuthorized(args))
        return trigger(args)
    }

    fun trigger(args: List<Any>): Thing<*>

    //If loaded from an external source, return that source.
    //If loaded from a class, return the class full name.
    fun serialize(): JsonElement

    companion object {
        fun urlToEvent(events: Events, url: String): Event {
            return try {
                val clazz = Class.forName(url).kotlin
                instantiateEvent(clazz, events)
            } catch (e: ClassNotFoundException) {
                val string = String(events.parent.io.loadResource(url).first)
                try {
                    val json = JsonParser.parseString(string)
                    InterpretedEvent.deserialize(events, json.asJsonObject)
                } catch (e: JsonParseException) {
                    InterpretedEvent.parse(events, string)
                }
            }
        }

        @JvmStatic
        fun instantiateEvent(clazz: KClass<*>, events: Events): Event {
            val instance = clazz.primaryConstructor!!.call(events)
            val annotation = clazz.annotations.filterIsInstance<HardcodedEvent>().first()
            val trigger = findMember(clazz, "trigger")
            val isAuthorized = findMember(clazz, "isAuthorized")
            val universe = events.parent.types.universe
            val parameters = ReflectionMember.argsToTypes(universe, trigger.valueParameters)
            val returnType = ReflectionMember.kTypeToType(universe, trigger.returnType)
            return object :
                AbstractEvent(
                    events,
                    annotation.name,
                    annotation.description,
                    Version(annotation.version),
                    parameters
                ) {
                override val returnType = returnType
                override fun isAuthorized(args: List<Any>): Boolean {
                    val argsWithSelf = listOf(instance) + args
                    return isAuthorized.call(*argsWithSelf.toTypedArray()) as Boolean
                }

                override fun trigger(args: List<Any>): Thing<*> {
                    val argsWithSelf = listOf(instance) + args
                    return returnType.wrap(trigger.call(*argsWithSelf.toTypedArray())!!)
                }

                override fun serialize(): JsonElement {
                    return JsonPrimitive(clazz.qualifiedName)
                }
            }
        }

        fun findMember(clazz: KClass<*>, memberName: String): KCallable<*> {
            return clazz.members.firstOrNull { it.name == memberName }
                ?: throw MemberNotFoundException(clazz, memberName)
        }
    }
}