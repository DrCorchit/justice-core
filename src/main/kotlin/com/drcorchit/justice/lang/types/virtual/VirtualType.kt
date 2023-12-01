package com.drcorchit.justice.lang.types.virtual


import com.drcorchit.justice.lang.types.Type
import com.google.common.collect.ImmutableList

interface VirtualType<T : Any> {

    fun makeType(parameters: ImmutableList<Type<*>>): Type<T>



}