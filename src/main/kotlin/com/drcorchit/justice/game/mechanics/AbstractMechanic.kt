package com.drcorchit.justice.game.mechanics

import com.drcorchit.justice.utils.json.JsonUtils.getOrDefault
import com.drcorchit.justice.utils.json.TimestampedJson
import com.drcorchit.justice.utils.json.info
import com.drcorchit.justice.utils.json.lastModified
import com.drcorchit.justice.utils.logging.Uri

abstract class AbstractMechanic<T : AbstractElement>(override val parent: Mechanics, override val name: String) : GameMechanic<T> {

    override val uri by lazy { parent.uri.extend(name) }

    override var lastModified = System.currentTimeMillis()

    override var defaultElement: T? = null

    override fun size(): Int {
        return elementsByUri.size
    }

    override fun has(key: Any): Boolean {
        return when (key) {
            is String -> elementsByName.containsKey(key)
            is Uri -> elementsByUri.containsKey(key)
            else -> false
        }
    }

    override fun get(key: Any): T {
        return when (key) {
            is String -> elementsByName[key]!!
            is Uri -> elementsByUri[key]!!
            else -> throw NoSuchElementException("No element in mechanic $name with key $key")
        }
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

        elementsByName.clear()

        //Mark all elements for deletion, then unmark them if they still exist
        val deleteThese: HashSet<T> = HashSet(elementsByUri.values)
        for (ele in info.getAsJsonArray("elements")) {
            //If it's a JsonNull, it's considered a "hidden" game element. (obscured by i.e. fog of war)
            if (ele.isJsonNull) continue
            val eleInfo = ele.asJsonObject

            //Use "key" if present, otherwise default to name
            val name = eleInfo["name"].asString
            val id = eleInfo.getOrDefault("key", { nextId() }, { it.asInt })
            val uri = this.uri.extend(id.toString())

            val element: T = elementsByUri.computeIfAbsent(uri) { create(name, uri) }
            element.sync(eleInfo)
            elementsByName[element.name] = element
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

        //Remove all elements still marked for deletion
        deleteThese.forEach { elementsByUri.remove(it.uri) }
        val message = String.format("Synced AbstractMechanic $name", name)
        logger.info("sync", message)
    }

    override fun iterator(): Iterator<T> {
        return elementsByUri.values.iterator()
    }

    //New (non-inherited) functionality
    protected var elementID = 0

    protected fun nextId(): Int {
        return elementID++
    }

    abstract fun create(name: String, uri: Uri): T

    protected val elementsByName: LinkedHashMap<String, T> = LinkedHashMap()

    protected val elementsByUri: LinkedHashMap<Uri, T> = LinkedHashMap()
}