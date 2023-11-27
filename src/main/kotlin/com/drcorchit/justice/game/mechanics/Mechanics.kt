package com.drcorchit.justice.game.mechanics

import com.drcorchit.justice.game.Game
import com.drcorchit.justice.lang.code.Thing
import com.drcorchit.justice.lang.members.LambdaFieldMember
import com.drcorchit.justice.lang.members.Member
import com.drcorchit.justice.lang.types.MechanicType
import com.drcorchit.justice.lang.types.NonSerializableType
import com.drcorchit.justice.lang.types.ReflectionType
import com.drcorchit.justice.lang.types.Type
import com.drcorchit.justice.utils.logging.HasUri
import com.drcorchit.justice.utils.logging.Uri
import com.google.common.collect.ImmutableMap
import com.google.errorprone.annotations.CanIgnoreReturnValue
import com.google.gson.JsonObject

interface Mechanics : Iterable<GameMechanic<*>>, HasUri {
    override val parent: Game
    override val uri: Uri get() = parent.uri.extend("mechanics")
    val asThing: Thing<Mechanics> get() = Thing(this, getType())

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

    fun getType(): Type<Mechanics>
    fun serialize(): JsonObject
    fun deserialize(info: JsonObject, timestamp: Long)

    fun makeEvaluator(): NonSerializableType<Mechanics> {
        val members = this.map {
            val type = ReflectionType(it::class, parent.types.universe, MechanicType)
            LambdaFieldMember(Mechanics::class.java, it.name, "Allows access to the ${it.name} mechanic.", type) { _ -> it }
        }.associateBy { it.name }.let { ImmutableMap.copyOf<String, Member<Mechanics>>(it) }

        return object : NonSerializableType<Mechanics>() {
            override val clazz = Mechanics::class.java
            override val members = members
        }
    }
}