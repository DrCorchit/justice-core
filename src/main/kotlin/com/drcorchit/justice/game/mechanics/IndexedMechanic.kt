package com.drcorchit.justice.game.mechanics

abstract class IndexedMechanic<T : AbstractElement>(parent: Mechanics, name: String) : AbstractMechanic<T>(parent, name) {

    protected var id = 0

    override fun getNextID(): String {
        return id++.toString()
    }
}