package com.drcorchit.justice.game.mechanics

import com.drcorchit.justice.game.Game
import com.drcorchit.justice.lang.evaluators.Evaluator
import com.drcorchit.justice.utils.json.info
import com.google.gson.JsonObject

class MechanicsImpl(override val parent: Game): Mechanics {
    private val evaluator: Evaluator<Mechanics> by lazy { Mechanics.makeEvaluator(this) }
    private val mechanics = LinkedHashMap<String, GameMechanic<*>>()

    override fun has(mechanic: String): Boolean {
        return mechanics.containsKey(mechanic)
    }

    override fun <T : GameMechanic<*>> get(mechanic: String): T {
        return mechanics[mechanic] as T
    }

    override fun serialize(): JsonObject {
        val output = JsonObject()
        mechanics.forEach { output.add(it.key, it.value.serialize()) }
        return output
    }

    override fun deserialize(info: JsonObject, timestamp: Long) {
        mechanics.clear()
        info.asJsonObject.entrySet().forEach {
            val json = if (it.value.isJsonObject) {
                it.value to timestamp
            } else {
                val path = it.value.asString
                parent.io.loadJson(path)
            }
            val className = json.info.asJsonObject["class"].asString
            val clazz = Class.forName(className) as Class<GameMechanic<*>>
            val mech = clazz.getConstructor(Game::class.java).newInstance(parent)
            mechanics[it.key] = mech
            mech.sync(json)
        }
    }

    override fun iterator(): Iterator<GameMechanic<*>> {
        return mechanics.values.iterator()
    }

    override fun getEvaluator(): Evaluator<Mechanics> {
        return evaluator
    }
}