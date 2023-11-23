package com.drcorchit.justice.lang.types

interface HasType<T : Any> {
    fun getType(): Type<T>
}