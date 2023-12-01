package com.drcorchit.justice.lang.members

import com.drcorchit.justice.game.Game
import com.google.gson.JsonElement

//Marker interface for members backed by an underlying field.
//Relevant during serialization.
interface DataFieldMember<T : Any>: FieldMember<T> {
    val mutable: Boolean
    val defaultValue: Any?

    fun set(self: T, newValue: Any)
    //Sets the value of the field even if it is marked as mutable.
    fun deserialize(self: T, game: Game, ele: JsonElement?)
}