package com.drcorchit.justice.game.mechanics

import com.google.gson.JsonElement
import com.google.gson.JsonPrimitive

data class MechID<T : GameMechanic<*>>(val name: String, val impl: Class<T>) {

    fun serialize(): JsonElement {
        return JsonPrimitive("$name@${impl.name}")
    }

    companion object {
        fun <T : GameMechanic<*>> deserialize(ele: JsonElement): MechID<T> {
            val str = ele.asString.split("@")
            val name = str[0]
            val clazz = Class.forName(str[1]) as Class<T>
            return MechID(name, clazz)
        }
    }
}