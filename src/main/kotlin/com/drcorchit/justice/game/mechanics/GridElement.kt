package com.drcorchit.justice.game.mechanics

import com.drcorchit.justice.utils.math.Space

abstract class GridElement(val coord: Space.Coordinate) : GameElement {
    override val id = "${coord.x},${coord.y}"

    override val uri by lazy { parent.uri.extend(coord.x.toString()).extend(coord.y.toString()) }

    override fun toString(): String {
        return "${parent.name}.${id}"
    }
}