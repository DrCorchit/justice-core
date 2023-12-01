package com.drcorchit.justice.game.mechanics.space

import com.drcorchit.justice.game.mechanics.AbstractElement
import com.drcorchit.justice.game.mechanics.AbstractMechanic
import com.drcorchit.justice.game.mechanics.Mechanics
import com.drcorchit.justice.lang.annotations.DataField
import com.drcorchit.justice.lang.annotations.Evaluable
import com.drcorchit.justice.utils.math.Space
import com.google.gson.JsonObject

//It's a namespace of named spaces...
class NamedSpaces(parent: Mechanics, name: String) : AbstractMechanic<NamedSpaces.NamedSpace>(parent, name) {

    inner class NamedSpace internal constructor(id: String) : AbstractElement(id) {
        override val parent = this@NamedSpaces

        @DataField("The underlying space of the object.")
        lateinit var space: Space

        @Evaluable("Creates a new coordinate depending on the underlying space.")
        fun getCoordinate(x: Int, y: Int): Coordinate {
            return Coordinate(this, space.coordinate(x, y))
        }
    }

    override fun create(id: String, info: JsonObject): NamedSpace {
        return NamedSpace(id)
    }
}

