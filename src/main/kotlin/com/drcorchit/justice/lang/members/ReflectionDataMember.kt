package com.drcorchit.justice.lang.members

import com.drcorchit.justice.game.Game
import com.drcorchit.justice.game.evaluation.TypeUniverse
import com.drcorchit.justice.lang.annotations.DataField
import com.google.gson.JsonElement
import kotlin.reflect.KMutableProperty

class ReflectionDataMember<T : Any>(types: TypeUniverse, clazz: Class<T>, override val member: KMutableProperty<*>, val annotation: DataField) :
    ReflectionMember<T>(types, clazz, member, annotation.description, false), DataFieldMember<T> {
    override val mutable = annotation.mutable

    init {
        require(argTypes.size == 1) { "Member $name is marked as a field, but has one or more arguments." }
    }

    override fun get(instance: T): Any {
        return member.getter.call(instance) ?: Unit
    }

    override fun set(self: T, newValue: Any) {
        check(mutable) { "Attempting to set immutable field $name" }
        member.setter.call(self, newValue)
    }

    override fun deserialize(self: T, game: Game, ele: JsonElement) {
        val newValue = returnType.deserialize(game, ele)
        member.setter.call(self, newValue)
    }
}