package com.drcorchit.justice.games

import com.drcorchit.utils.Logger
import com.drcorchit.utils.json.getOrDefault
import com.google.gson.JsonObject

private val log = Logger.getLogger(AbstractMechanic::class.java)

abstract class AbstractMechanic<T : AbstractElement>(game: Game, info: JsonObject, date: Long) : GameMechanic<T> {

    init {
        sync(info)
    }

    override val parent: Game = game

    override var lastModified: Long = date

    override var defaultElement: T? = null

    override fun size(): Int {
        return elementsById.size
    }

    override fun has(key: Any): Boolean {
        return when (key) {
            is String -> elementsByName.containsKey(key)
            is Int -> elementsById.containsKey(key)
            else -> false
        }
    }

    override fun get(key: Any): T {
        return when (key) {
            is String -> elementsByName[key]!!
            is Int -> elementsById[key]!!
            else -> throw NoSuchElementException("No element in mechanic $name with key $key")
        }
    }

    override fun touch() {
        lastModified = System.currentTimeMillis()
    }

    final override fun sync(info: JsonObject) {
        if (!info.has("elements")) {
            val message = "Mechanic info for $name is missing \"elements\". Found keys: ${info.keySet()}"
            throw IllegalArgumentException(message)
        }
        preSync(info)

        elementsByName.clear()

        //Mark all elements for deletion, then unmark them if they still exist
        val deleteThese: HashSet<T> = HashSet(elementsById.values)
        for (ele in info.getAsJsonArray("elements")) {
            //If it's a JsonNull, it's considered a "hidden" game element. (obscured by i.e. fog of war)
            if (ele.isJsonNull) continue
            val eleInfo = ele.asJsonObject

            //Use "key" if present, otherwise default to name
            val name = eleInfo["name"].asString
            val key = eleInfo.getOrDefault("key", { nextId() }, { it.asInt })

            var element: T
            if (has(key)) {
                element = get(key)
                deleteThese.remove(element)
            } else {
                element = create(name, key)
                elementsById[key] = element
            }
            element.sync(eleInfo)
            elementsByName[element.name()] = element
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
        deleteThese.forEach { elementsById.remove(it.key) }
        postSync(info)
        val message = String.format("Synced AbstractMechanic $name", name)
        log.info("sync", message)
    }


    override fun iterator(): Iterator<T> {
        return elementsById.values.iterator()
    }

    //New (non-inherited) functionality
    protected var id = 0

    protected fun nextId(): Int {
        return id++
    }

    abstract fun create(name: String, id: Int): T

    protected val elementsByName: LinkedHashMap<String, T> = LinkedHashMap()

    protected val elementsById: LinkedHashMap<Int, T> = LinkedHashMap()
}