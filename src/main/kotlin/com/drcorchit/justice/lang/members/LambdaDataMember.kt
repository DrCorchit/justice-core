package com.drcorchit.justice.lang.members

import com.drcorchit.justice.game.Game
import com.drcorchit.justice.lang.evaluators.Evaluator
import com.google.gson.JsonElement

class LambdaDataMember<T : Any>(
    name: String,
    description: String,
    returnType: Evaluator<*>,
    getter: (T) -> Any?,
    private val setter: ((T, Any) -> Unit),
    override val mutable: Boolean
) : LambdaFieldMember<T>(name, description, returnType, getter), DataFieldMember<T> {

    override fun set(self: T, newValue: Any) {
        require(mutable) { "Attempting to set immutable field $name!" }
        setter.invoke(self, newValue)
    }

    override fun deserialize(self: T, game: Game, ele: JsonElement) {
        val value = returnType!!.deserialize(game, ele)
        setter.invoke(self, value)
    }
}