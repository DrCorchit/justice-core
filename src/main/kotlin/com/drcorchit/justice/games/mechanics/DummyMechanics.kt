package com.drcorchit.justice.games.mechanics

import com.drcorchit.justice.games.DummyGame
import java.util.*

class DummyMechanics(override val game: DummyGame): Mechanics {
    override fun has(mechanic: MechID<*>): Boolean {
        return false
    }

    override fun <T : GameMechanic<*>> get(mechanic: MechID<T>): T {
        throw NoSuchElementException("No mechanics in DummyGame")
    }

    override fun iterator(): Iterator<GameMechanic<*>> {
        return Collections.emptyIterator()
    }
}