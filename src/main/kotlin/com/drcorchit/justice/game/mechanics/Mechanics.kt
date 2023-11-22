package com.drcorchit.justice.game.mechanics

import com.drcorchit.justice.game.Game
import com.drcorchit.justice.lang.evaluators.HasEvaluator
import com.drcorchit.justice.lang.evaluators.NonSerializableEvaluator
import com.drcorchit.justice.lang.members.LambdaFieldMember
import com.drcorchit.justice.lang.members.Member
import com.drcorchit.justice.utils.logging.HasUri
import com.drcorchit.justice.utils.logging.Uri
import com.google.common.collect.ImmutableMap
import com.google.errorprone.annotations.CanIgnoreReturnValue
import com.google.gson.JsonObject

interface Mechanics : Iterable<GameMechanic<*>>, HasUri, HasEvaluator<Mechanics> {
    override val parent: Game
    override val uri: Uri get() = parent.uri.extend("mechanics")

    fun has(mechanic: String): Boolean

    //Throws NoSuchElementException if not found.
    operator fun <T : GameMechanic<*>> get(mechanic: String): T

    //Convenience method
    fun <K : GameMechanic<*>, V> with(mechanic: String, task: (K) -> V, def: V? = null): V {
        return if (has(mechanic)) task.invoke(get(mechanic))
        else def ?: throw IllegalStateException("Mechanic $mechanic not found in game ${parent.id}.")
    }

    //Convenience method
    @CanIgnoreReturnValue
    fun <T : GameMechanic<*>> doWith(mechanic: String, task: (T) -> Unit): Boolean {
        return if (has(mechanic)) {
            task.invoke(get(mechanic))
            return true
        } else false
    }

    fun serialize(): JsonObject
    fun deserialize(info: JsonObject, timestamp: Long)

    companion object {
        fun makeEvaluator(mechanics: Mechanics): NonSerializableEvaluator<Mechanics> {
            val builder = ImmutableMap.builder<String, Member<Mechanics>>()
            mechanics.forEach {
                builder.put(
                    it.name,
                    LambdaFieldMember(it.name, it.description, it.getEvaluator()) { _ -> it })
            }

            return object : NonSerializableEvaluator<Mechanics>() {
                override val clazz = Mechanics::class
                override val members = builder.build()
            }
        }
    }
}