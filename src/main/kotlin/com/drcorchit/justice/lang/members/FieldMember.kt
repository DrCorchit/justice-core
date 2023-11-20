package com.drcorchit.justice.lang.members

interface FieldMember<T> {
    fun get(instance: T): Any?
}