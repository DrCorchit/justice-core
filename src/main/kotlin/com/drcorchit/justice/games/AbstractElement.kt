package com.drcorchit.justice.games

import com.drcorchit.utils.json.getString
import com.google.gson.JsonObject

abstract class AbstractElement(private val parent: AbstractMechanic<*>, name: String, private val id: Int) : GameElement {

    protected var info: JsonObject = JsonObject()

    protected var name: String = name
        set(name) {
            field = name
            touch()
        }

    override val key: Int
        get() = id

    override fun name(): String {
        return name
    }

    override fun description(): String {
        return info.getString("description", "No description is available.")
    }

    override fun parent(): AbstractMechanic<*> {
        return parent
    }

    override fun sync(info: JsonObject) {
        this.info = info.deepCopy()
        touch()
    }
}