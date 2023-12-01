package com.drcorchit.justice.game.mechanics.space

import com.drcorchit.justice.utils.math.Space

data class Coordinate(val parent: NamedSpaces.NamedSpace, val coordinate: Space.Coordinate) {
    val space get() = parent.space
    val x get() = coordinate.x
    val y get() = coordinate.y

    override fun toString(): String {
        //If the coordinate belongs to the default NamedSpace, omit the explicitly NamedSpace name
        val base = "$x,$y"
        val parentUri = "${parent.parent.name}.${parent.uri.value}"
        return if (parent == parent.parent.defaultElement) base else "$parentUri.$base"
    }
}