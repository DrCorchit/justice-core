package com.drcorchit.justice.game.mechanics

import com.drcorchit.justice.game.mechanics.space.SpaceType
import com.drcorchit.justice.utils.data.Grid
import com.drcorchit.justice.utils.json.TimestampedJson
import com.drcorchit.justice.utils.json.info
import com.drcorchit.justice.utils.math.Space
import com.google.common.collect.ImmutableList
import com.google.gson.JsonArray
import com.google.gson.JsonNull
import com.google.gson.JsonObject

abstract class GridMechanic<T : GridElement>(override val parent: Mechanics, override val name: String) :
    GameMechanic<T> {

    override var lastModified: Long = System.currentTimeMillis()

    override var defaultElement: T? = null

    override fun size(): Int {
        return grid.space.size
    }

    override fun has(id: String): Boolean {
        return try {
            val c = grid.space.parse(id)
            grid.space.within(c.x, c.y)
        } catch (e: Exception) {
            false
        }
    }

    override fun get(id: String): T {
        val c = grid.space.parse(id)
        return grid.get(c)!!
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

        val space = Space.deserialize(info.getAsJsonObject("space"))
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
                get(info.get("default").asString)
            } else null

        val message = String.format("Synced GridMechanic $name", name)
        logger.info("sync", message)
    }


    override fun iterator(): Iterator<T> {
        return grid.iterator()
    }

    override fun serialize(): JsonObject {
        val universe = parent.parent.types.universe
        val type = universe.getType(this::class to ImmutableList.of())
        val output = type.serializeByReflection(this).asJsonObject
        val rows = JsonArray()
        for (j in 0 until grid.height) {
            val row = JsonArray()
            for (i in 0 until grid.width) {
                row.add(grid[i, j]?.serialize() ?: JsonNull.INSTANCE)
            }
            rows.add(row)
        }
        output.add("space", SpaceType.serialize(grid.space))
        output.add("elements", rows)
        defaultElement?.let { output.addProperty("default", it.uri.toString()) }

        return output
    }

    //New (non-inherited) functionality
    var grid: Grid<T> = Grid(0, 0)

    abstract fun create(coordinate: Space.Coordinate): T

}