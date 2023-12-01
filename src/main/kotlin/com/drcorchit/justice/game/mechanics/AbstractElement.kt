package com.drcorchit.justice.game.mechanics

abstract class AbstractElement(override val id: String) : GameElement {
    override val uri by lazy { parent.uri.extend(id) }

    override fun toString(): String {
        return "${parent.name}.${id}"
    }

    override fun equals(other: Any?): Boolean {
        return other is AbstractElement && other.uri == uri
    }

    override fun hashCode(): Int {
        return uri.hashCode()
    }
}