package com.drcorchit.justice.game.mechanics

import com.drcorchit.justice.utils.json.JsonUtils.getOrDefault
import com.drcorchit.justice.utils.json.TimestampedJson
import com.drcorchit.justice.utils.json.info
import com.drcorchit.justice.utils.json.lastModified
import com.drcorchit.justice.utils.logging.Uri
import com.google.gson.JsonObject

abstract class AbstractMechanic<T : AbstractElement>(override val parent: Mechanics, override val name: String) :
    GameMechanic<T> {

    override val uri by lazy { parent.uri.extend(name) }

    override var lastModified = System.currentTimeMillis()

    override var defaultElement: T? = null

    override fun size(): Int {
        return elementsByUri.size
    }

    override fun has(uri: Uri): Boolean {
        return elementsByUri.containsKey(uri)
    }

    override fun get(uri: Uri): T {
        return elementsByUri[uri] ?: throw NoSuchElementException("No such element in $name: $uri")
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

        //Mark all elements for deletion, then unmark them if they still exist
        val deleteThese: HashSet<T> = HashSet(elementsByUri.values)
        for (ele in info.getAsJsonArray("elements")) {
            //If it's a JsonNull, it's considered a "hidden" game element. (obscured by i.e. fog of war)
            if (ele.isJsonNull) continue
            val eleInfo = ele.asJsonObject

            //Use "key" if present, otherwise default to name
            val uri = eleInfo.getOrDefault("uri", { nextUri() }, { Uri.parse(it.asString) })
            val element: T = elementsByUri.computeIfAbsent(uri) { create(uri, eleInfo) }
            element.sync(eleInfo)
        }

        defaultElement =
            if (info.has("default")) {
                val defaultUri = Uri.parse(info.get("default").asString)
                get(defaultUri)
            } else null

        //Remove all elements still marked for deletion
        deleteThese.forEach { elementsByUri.remove(it.uri) }
        val message = String.format("Synced AbstractMechanic $name", name)
        logger.info("sync", message)
    }

    override fun iterator(): Iterator<T> {
        return elementsByUri.values.iterator()
    }

    override fun equals(other: Any?): Boolean {
        return other is AbstractMechanic<*> && other.uri == uri
    }

    override fun hashCode(): Int {
        return uri.hashCode()
    }

    override fun serialize(): JsonObject {
        val output = JsonObject()


        return output
    }

    //New (non-inherited) functionality
    protected var nextID = 0

    protected fun nextUri(): Uri {
        return uri.extend(nextID++.toString())
    }

    abstract fun create(uri: Uri, info: JsonObject): T

    protected val elementsByUri: LinkedHashMap<Uri, T> = LinkedHashMap()


}