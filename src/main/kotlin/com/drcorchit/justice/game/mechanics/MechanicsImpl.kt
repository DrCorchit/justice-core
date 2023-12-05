package com.drcorchit.justice.game.mechanics

import com.drcorchit.justice.game.Game
import com.drcorchit.justice.game.mechanics.Mechanics.Companion.makeEvaluator
import com.drcorchit.justice.lang.types.Type
import com.drcorchit.justice.utils.json.info
import com.drcorchit.justice.utils.json.toJson
import com.google.gson.JsonObject
import kotlin.reflect.full.primaryConstructor

class MechanicsImpl(override val parent: Game) : Mechanics {
    private val mechType: Type<Mechanics> by lazy { this.makeEvaluator() }
    private val mechanics = LinkedHashMap<String, GameMechanic<*>>()

    override fun has(mechanic: String): Boolean {
        return mechanics.containsKey(mechanic)
    }

    override fun <T : GameMechanic<*>> get(mechanic: String): T {
        return mechanics[mechanic] as? T ?:
        throw NoSuchElementException("No mechanic named $mechanic in ${parent.id}")
    }

    override fun serialize(): JsonObject {
        val output = JsonObject()
        mechanics.forEach { output.add(it.key, it.value.serialize()) }
        return output
    }

    override fun sync(info: JsonObject, timestamp: Long) {
        info.asJsonObject.entrySet().forEach {
            val name = it.key
            val json = if (it.value.isJsonObject) {
                it.value to timestamp
            } else {
                val path = it.value.asString
                parent.io.loadResource(path).toJson()
            }

            //Create the mechanic if it is missing.
            mechanics.computeIfAbsent(name) {
                val className = json.info.asJsonObject["class"].asString
                val clazz = (Class.forName(className) as Class<out GameMechanic<*>>).kotlin
                clazz.primaryConstructor!!.call(this, name)
            }.sync(json)
        }
    }

    override fun iterator(): Iterator<GameMechanic<*>> {
        return mechanics.values.iterator()
    }

    override fun getType(): Type<Mechanics> {
        return mechType
    }
}