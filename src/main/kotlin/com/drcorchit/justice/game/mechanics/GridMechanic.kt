package com.drcorchit.justice.game.mechanics

import com.drcorchit.justice.utils.data.Grid
import com.drcorchit.justice.utils.json.JsonUtils.getBool
import com.drcorchit.justice.utils.json.JsonUtils.getOrDefault
import com.drcorchit.justice.utils.json.TimestampedJson
import com.drcorchit.justice.utils.json.info
import com.drcorchit.justice.utils.math.Layout
import com.drcorchit.justice.utils.math.Space

abstract class GridMechanic<T : GridElement>(override val parent: Mechanics, override val name: String) : GameMechanic<T> {

    override var lastModified: Long = System.currentTimeMillis()

    override var defaultElement: T? = null

    override fun size(): Int {
        return grid.space.size
    }

    override fun has(key: Any): Boolean {
        return if (key is Space.Coordinate) {
            grid.space.within(key.x, key.y)
        } else throw java.lang.IllegalArgumentException("Supplied key is not a valid coordinate")
    }

    override fun get(key: Any): T {
        if (key is Space.Coordinate) {
            return grid.get(key)!!
        } else {
            throw IllegalArgumentException("Supplied key is not a valid coordinate")
        }
    }

    override fun touch() {
        lastModified = System.currentTimeMillis()
    }

    final override fun sync(json: TimestampedJson) {
        val info = json.info.asJsonObject
        if (!info.has("elements")) {
            val message = "Mechanic info for $name is missing \"elements\". Found keys: ${info.keySet()}"
            throw IllegalArgumentException(message)
        }

        val w = info["width"].asInt
        val h = info["height"].asInt
        val wrapH = info.getBool("wrapHoriz", false)
        val wrapV = info.getBool("wrapVert", false)
        val layout = info.getOrDefault("layout", { Layout.CARTESIAN }, { Layout.valueOf(it.asString) })
        val space = Space(w, h, wrapH, wrapV, layout)
        if (space != grid.space) {
            logger.info("sync", "Updating grid mechanic grid due to underlying space change.")
            grid = Grid(space)
        }

        val rows = info.getAsJsonArray("elements").asJsonArray
        if (rows.size() != grid.height) {
            throw IllegalArgumentException("Incorrect grid size; expected ${grid.height} but got ${rows.size()}")
        }

        for ((j, row) in rows.withIndex()) {
            val actualWidth = row.asJsonArray.size()
            if (actualWidth != grid.width) {
                throw java.lang.IllegalArgumentException("Incorrect grid size; expected ${grid.width} but got $actualWidth")
            }
            for ((i, ele) in row.asJsonArray.withIndex()) {
                val eleInfo = ele.asJsonObject
                val element: T = grid[i, j] ?: create(grid.space.coordinate(i, j))
                element.sync(eleInfo)
            }
        }

        defaultElement =
            if (info.has("default")) {
                val rawKey = info["default"].asJsonPrimitive
                if (rawKey.isString) get(rawKey.asString)
                else if (rawKey.isNumber) get(rawKey.asInt)
                else throw IllegalArgumentException("That type of JsonPrimitive cannot specify a default element")
            } else {
                null
            }

        val message = String.format("Synced GridMechanic $name", name)
        logger.info("sync", message)
    }


    override fun iterator(): Iterator<T> {
        return grid.iterator()
    }

    //New (non-inherited) functionality
    var grid: Grid<T> = Grid(0, 0)

    abstract fun create(coordinate: Space.Coordinate): T

}