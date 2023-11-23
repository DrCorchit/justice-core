package com.drcorchit.justice.lang.types

import com.drcorchit.justice.exceptions.JusticeException
import com.drcorchit.justice.exceptions.MemberNotFoundException
import com.drcorchit.justice.lang.members.DataFieldMember
import com.drcorchit.justice.lang.members.Member
import com.drcorchit.justice.lang.types.primitives.*
import com.google.gson.JsonElement

data class TypedThing<T : Any>(val thing: T, val type: Type<T>) {

    fun serialize(): JsonElement {
        return type.serialize(thing)
    }

    fun evaluateMember(name: String, args: List<Any>): TypedThing<*> {
        return type.getMember(name)!!.applyAndWrap(thing, args)
    }

    fun assignMember(name: String, newValue: Any) {
        val member: Member<T>? = type.getMember(name)
        if (member == null) {
            throw MemberNotFoundException(type, name)
        } else if (member is DataFieldMember<T>) {
            member.set(thing, type.cast(newValue))
        }
    }

    fun setOrPut(index: Any, newValue: Any): TypedThing<*> {
        val member: Member<T>? = type.getMember("set") ?: type.getMember("put")
        if (member == null) {
            throw MemberNotFoundException(type, "set/put")
        } else {
            //TODO check types here, maybe?
            return member.applyAndWrap(thing, listOf(index, newValue))
        }
    }

    companion object {
        val UNIT = TypedThing(Unit, UnitType)
        val TRUE = TypedThing(true, BooleanType)
        val FALSE = TypedThing(false, BooleanType)
        val PI = TypedThing(Math.PI, RealType)
        val E = TypedThing(Math.E, RealType)

        fun wrapPrimitive(value: Any): TypedThing<*> {
            return when (value) {
                is Boolean -> TypedThing(value, BooleanType)
                is Int -> TypedThing(value, IntType)
                is Long -> TypedThing(value, LongType)
                is Number -> TypedThing(value.toDouble(), RealType)
                is String -> TypedThing(value, StringType)
                else -> throw JusticeException("$value is not a primitive type (Bool, Number, String)")
            }
        }
    }
}