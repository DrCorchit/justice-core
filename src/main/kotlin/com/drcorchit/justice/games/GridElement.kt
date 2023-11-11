package com.drcorchit.justice.games

import com.drcorchit.justice.utils.json.getString
import com.drcorchit.justice.utils.math.Space
import com.google.gson.JsonObject

abstract class GridElement(val parent: GridMechanic<*>, val coord: Space.Coordinate) : GameElement {

    protected var info = JsonObject()

    override fun name(): String {
        return coord.toString()
    }

    override fun description(): String {
        return info.getString("description", "No description is available.")
    }

    override fun parent(): GridMechanic<*> {
        return parent
    }

    override fun sync(info: JsonObject) {
        preSync(info)
        this.info = info
        postSync(info)
        touch()
    }
}