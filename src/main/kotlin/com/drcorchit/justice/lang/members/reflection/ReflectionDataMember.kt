package com.drcorchit.justice.lang.members.reflection

import com.drcorchit.justice.exceptions.DeserializationException
import com.drcorchit.justice.game.Game
import com.drcorchit.justice.game.evaluation.universe.TypeUniverse
import com.drcorchit.justice.lang.annotations.DataField
import com.drcorchit.justice.lang.members.DataFieldMember
import com.drcorchit.justice.lang.types.Type
import com.drcorchit.justice.lang.types.primitives.*
import com.google.gson.JsonElement
import kotlin.reflect.KMutableProperty

class ReflectionDataMember<T : Any>(
    types: TypeUniverse,
    type: Type<in T>,
    override val member: KMutableProperty<*>,
    val annotation: DataField
) :
    ReflectionMember<T>(types, type, member, annotation.description, false), DataFieldMember<T> {
    override val mutable = annotation.mutable

    override val defaultValue: Any?

    init {
        require(parameters.entries.isEmpty()) { "Member $name is marked as a field, but has one or more arguments." }

        val defStr = annotation.defaultValue
        defaultValue = if (defStr.isEmpty()) null
        else {
            when (returnType) {
                is BooleanType -> defStr.toBooleanStrict()
                is IntType -> defStr.toInt()
                is LongType -> defStr.toLong()
                is RealType, NumberType -> defStr.toDouble()
                is StringType -> defStr
                //TODO
                //is EnumType -> returnType.deserialize()
                else -> null
            }
        }
    }

    override fun get(instance: T): Any {
        return member.getter.call(instance) ?: Unit
    }

    override fun set(self: T, newValue: Any) {
        check(mutable) { "Attempting to set immutable field $name" }
        member.setter.call(self, newValue)
    }

    override fun deserialize(self: T, game: Game, ele: JsonElement?) {
        val newValue = ele?.let { returnType.deserialize(game, it) } ?: defaultValue
        ?: throw DeserializationException("No json value or default defined for field $name")
        member.setter.call(self, newValue)
    }
}