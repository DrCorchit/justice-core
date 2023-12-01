package com.drcorchit.justice.lang.members.lambda

import com.drcorchit.justice.game.Game
import com.drcorchit.justice.lang.members.DataFieldMember
import com.drcorchit.justice.lang.types.Type
import com.google.gson.JsonElement

class LambdaDataMember<T : Any>(
    type: Type<in T>,
    name: String,
    description: String,
    override val defaultValue: Any?,
    returnType: Type<*>,
    getter: (T) -> Any,
    private val setter: ((T, Any) -> Unit),
    override val mutable: Boolean
) : LambdaFieldMember<T>(type, name, description, returnType, getter), DataFieldMember<T> {

    override fun set(self: T, newValue: Any) {
        require(mutable) { "Attempting to set immutable field $name!" }
        setter.invoke(self, newValue)
    }

    override fun deserialize(self: T, game: Game, ele: JsonElement?) {
        val newValue = ele?.let { returnType.deserialize(game, it) } ?: defaultValue!!
        setter.invoke(self, newValue)
    }
}