package com.drcorchit.justice.lang.types

//TODO delete this interface. We want to be using TypedThing instead.
interface HasType<T : Any> {
    fun getType(): Type<T>
}