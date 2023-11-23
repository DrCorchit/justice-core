package com.drcorchit.justice.game.mechanics

import com.drcorchit.justice.game.Game
import com.drcorchit.justice.lang.members.LambdaFieldMember
import com.drcorchit.justice.lang.types.HasType
import com.drcorchit.justice.lang.types.NonSerializableType
import com.drcorchit.justice.utils.logging.HasUri
import com.drcorchit.justice.utils.logging.Uri
import com.google.common.collect.ImmutableMap
import com.google.errorprone.annotations.CanIgnoreReturnValue
import com.google.gson.JsonObject

interface Mechanics : Iterable<GameMechanic<*>>, HasUri, HasType<Mechanics> {
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

    fun makeEvaluator(): NonSerializableType<Mechanics> {
        val members = this.map {
            val type = parent.types.source.typeOfInstance(it)
            LambdaFieldMember(Mechanics::class.java, it.name, it.description, type) { _ -> it }
        }.associateBy { it.name }.let { ImmutableMap.copyOf(it) }

        return object : NonSerializableType<Mechanics>() {
            override val clazz = Mechanics::class.java
            override val members = members
        }
    }
}