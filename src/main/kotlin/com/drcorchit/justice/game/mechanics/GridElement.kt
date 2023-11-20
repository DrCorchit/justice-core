package com.drcorchit.justice.game.mechanics

import com.drcorchit.justice.utils.math.Space

abstract class GridElement(override val parent: GridMechanic<*>, val coord: Space.Coordinate) : GameElement {

    override var name = coord.toString()

    override val uri by lazy { parent.uri.extend(coord.x.toString()).extend(coord.y.toString()) }

    override var description: String = "No description available."
}