package com.drcorchit.justice.lang.types

import com.google.gson.JsonElement

data class TypedThing<T : Any>(val thing: T, val type: Type<T>) {

    fun serialize(): JsonElement {
        return type.serialize(thing)
    }

    fun evaluateMember(name: String, args: List<Any>): TypedThing<*> {
        val member = type.getMember(name)!!
        return member.apply(thing, args), member.returnType
    }
}