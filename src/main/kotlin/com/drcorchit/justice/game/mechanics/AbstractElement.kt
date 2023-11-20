package com.drcorchit.justice.game.mechanics

abstract class AbstractElement(final override val parent: AbstractMechanic<*>, name: String, id: Int) : GameElement {
    override var name: String = name
        set(name) {
            field = name
            touch()
        }

    override val uri by lazy { parent.uri.extend(id.toString()) }

    override var description: String = "No description available."
}