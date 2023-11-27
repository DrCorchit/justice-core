package com.drcorchit.justice.game.mechanics

import com.drcorchit.justice.utils.logging.Uri

abstract class AbstractElement(override val uri: Uri) : GameElement {

    override fun toString(): String {
        return uri.toString()
    }

    override fun equals(other: Any?): Boolean {
        return other is AbstractElement && other.uri == uri
    }

    override fun hashCode(): Int {
        return uri.hashCode()
    }
}