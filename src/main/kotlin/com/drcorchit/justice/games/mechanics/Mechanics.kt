package com.drcorchit.justice.games.mechanics

import com.drcorchit.justice.games.Game
import com.google.errorprone.annotations.CanIgnoreReturnValue
import java.util.function.Consumer
import java.util.function.Function

interface Mechanics: Iterable<GameMechanic<*>> {
    val game: Game

    fun has(mechanic: MechID<*>): Boolean

    //Throws NoSuchElementException if not found.
    operator fun <T : GameMechanic<*>> get(mechanic: MechID<T>): T

    //Convenience method
    fun <K : GameMechanic<*>, V> with(mechanic: MechID<K>, task: Function<K, V>, def: V? = null): V {
        return if (has(mechanic)) task.apply(get(mechanic))
        else def ?: throw IllegalStateException("Mechanic $mechanic not found in game ${game.id}.")
    }

    //Convenience method
    @CanIgnoreReturnValue
    fun <T : GameMechanic<*>> doWith(mechanic: MechID<T>, task: Consumer<T>): Boolean {
        return if (has(mechanic)) {
            task.accept(get(mechanic))
            return true
        } else false
    }
}