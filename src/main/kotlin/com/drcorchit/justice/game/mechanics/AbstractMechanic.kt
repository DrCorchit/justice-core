package com.drcorchit.justice.game.mechanics

import com.drcorchit.justice.utils.json.JsonUtils.getOrDefault
import com.drcorchit.justice.utils.json.JsonUtils.toJsonArray
import com.drcorchit.justice.utils.json.TimestampedJson
import com.drcorchit.justice.utils.json.info
import com.drcorchit.justice.utils.json.lastModified
import com.google.common.collect.ImmutableList
import com.google.gson.JsonObject

abstract class AbstractMechanic<T : AbstractElement>(override val parent: Mechanics, override val name: String) :
    GameMechanic<T> {

    override val uri by lazy { parent.uri.extend(name) }

    override var lastModified = System.currentTimeMillis()

    override var defaultElement: T? = null

    override fun size(): Int {
        return elementsByID.size
    }

    override fun has(id: String): Boolean {
        return elementsByID.containsKey(id)
    }

    override fun get(id: String): T {
        return elementsByID[id] ?: throw NoSuchElementException("No such element in $name: $id")
    }

    override fun touch() {
        lastModified = System.currentTimeMillis()
    }

    override fun sync(json: TimestampedJson) {
        if (json.lastModified > System.currentTimeMillis()) {
            logger.warn("sync", "Time-travelling JSON detected.")
        }
        val info = json.info.asJsonObject
        if (!info.has("elements")) {
            val message = "Mechanic info for $name is missing \"elements\". Found keys: ${info.keySet()}"
            throw IllegalArgumentException(message)
        }

        val universe = parent.parent.types.universe
        val type = universe.getType(this::class to ImmutableList.of())
        type.sync(this, parent.parent, info)

        for (ele in info.getAsJsonArray("elements")) {
            //If it's a JsonNull, it's considered a "hidden" game element. (obscured by i.e. fog of war)
            if (ele.isJsonNull) continue
            val eleInfo = ele.asJsonObject

            //Use "key" if present, otherwise default to name
            val id = eleInfo.getOrDefault("id", { getNextID() }, { it.asString })
            val element: T = elementsByID.computeIfAbsent(id) { create(id, eleInfo) }
            element.sync(eleInfo)
        }

        defaultElement =
            if (info.has("default")) {
                val default = info.get("default").asString
                if (default == "_") elementsByID.values.first()
                else get(default)
            } else null

        lastModified = json.lastModified
        val message = String.format("Synced AbstractMechanic $name", name)
        logger.info("sync", message)
    }

    override fun iterator(): Iterator<T> {
        return elementsByID.values.iterator()
    }

    override fun equals(other: Any?): Boolean {
        return other is AbstractMechanic<*> && other.uri == uri
    }

    override fun hashCode(): Int {
        return uri.hashCode()
    }

    override fun serialize(): JsonObject {
        val universe = parent.parent.types.universe
        val type = universe.getType(this::class to ImmutableList.of())
        val output = type.serializeByReflection(this).asJsonObject
        defaultElement?.let { output.addProperty("default", it.id) }
        val elementsInfo = elementsByID.values.map { it.serialize() }.toJsonArray()
        output.add("elements", elementsInfo)
        return output
    }

    //New functionality (not override)
    abstract fun create(id: String, info: JsonObject): T

    open fun getNextID(): String {
        throw UnsupportedOperationException("Element ID creation not supported by GameMechanic $name")
    }

    protected val elementsByID: LinkedHashMap<String, T> = LinkedHashMap()
}