package com.drcorchit.justice.game.mechanics

import com.drcorchit.justice.game.Game
import com.drcorchit.justice.lang.members.Member
import com.drcorchit.justice.lang.members.lambda.LambdaFieldMember
import com.drcorchit.justice.lang.types.GameMechanicType
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

    fun has(mechanic: String): Boolean

    //Throws NoSuchElementException if not found.
    operator fun <T : GameMechanic<*>> get(mechanic: String): T

    fun getType(): Type<Mechanics>
    fun serialize(): JsonObject
    fun sync(info: JsonObject, timestamp: Long)

    companion object {
        //Convenience method
        inline fun <reified K : GameMechanic<*>, V> Mechanics.with(mechanic: String, task: (K) -> V, def: V? = null): V {
            return if (has(mechanic)) task.invoke(get(mechanic))
            else def ?: throw IllegalStateException("Mechanic $mechanic not found in game ${parent.id}.")
        }

        //Convenience method
        @CanIgnoreReturnValue
        inline fun <reified T : GameMechanic<*>> Mechanics.doWith(mechanic: String, task: (T) -> Unit): Boolean {
            return if (has(mechanic)) {
                task.invoke(get(mechanic))
                return true
            } else false
        }

        fun Mechanics.makeEvaluator(): NonSerializableType<Mechanics> {
            return object : NonSerializableType<Mechanics>(Mechanics::class) {
                override val members: ImmutableMap<String, Member<Mechanics>>
                init {
                    val mechs = this@makeEvaluator
                    members = mechs.map {
                        val type = ReflectionType(it::class, mechs.parent.types.universe, GameMechanicType)
                        LambdaFieldMember(this, it.name, "Allows access to the ${it.name} mechanic.", type) { _ -> it }
                    }.associateBy { it.name }.let { ImmutableMap.copyOf<String, Member<Mechanics>>(it) }
                }
            }
        }
    }
}