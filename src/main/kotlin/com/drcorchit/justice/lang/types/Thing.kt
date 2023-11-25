package com.drcorchit.justice.lang.types

import com.drcorchit.justice.exceptions.JusticeException
import com.drcorchit.justice.exceptions.MemberNotFoundException
import com.drcorchit.justice.lang.members.DataFieldMember
import com.drcorchit.justice.lang.members.FieldMember
import com.drcorchit.justice.lang.members.Member
import com.drcorchit.justice.lang.types.primitives.*
import com.google.gson.JsonElement

data class Thing<T : Any>(val value: T, val type: Type<T>) {

    fun serialize(): JsonElement {
        return type.serialize(value)
    }

    fun getMember(name: String): Thing<Member<*>> {
        //return TypedThing(type.getMember(name)!!, MemberType)
        return MemberType.wrap(type.getMember(name)!!)
    }

    fun runMember(name: String, args: List<Any>): Thing<*> {
        return when (val member = type.getMember(name)) {
            null -> throw MemberNotFoundException(type.clazz, name)
            is FieldMember<T> -> member.getAndWrap(value)
            else -> member.applyAndWrap(value, args)
        }
    }

    fun assignMember(name: String, newValue: Any) {
        val member: Member<T>? = type.getMember(name)
        if (member == null) {
            throw MemberNotFoundException(type.clazz, name)
        } else if (member is DataFieldMember<T>) {
            member.set(value, type.cast(newValue))
        }
    }

    fun setOrPut(index: Any, newValue: Any): Thing<*> {
        val member: Member<T>? = type.getMember("set") ?: type.getMember("put")
        if (member == null) {
            throw MemberNotFoundException(type.clazz, "set/put")
        } else {
            return member.applyAndWrap(value, listOf(index, type.cast(newValue)))
        }
    }

    companion object {
        val UNIT = Thing(Unit, UnitType)
        val TRUE = Thing(true, BooleanType)
        val FALSE = Thing(false, BooleanType)
        val PI = Thing(Math.PI, RealType)
        val E = Thing(Math.E, RealType)

        fun wrapPrimitive(value: Any): Thing<*> {
            return when (value) {
                is Boolean -> Thing(value, BooleanType)
                is Int -> Thing(value, IntType)
                is Long -> Thing(value, LongType)
                is Number -> Thing(value.toDouble(), RealType)
                is String -> Thing(value, StringType)
                else -> throw JusticeException("$value is not a primitive type (Bool, Number, String)")
            }
        }
    }
}