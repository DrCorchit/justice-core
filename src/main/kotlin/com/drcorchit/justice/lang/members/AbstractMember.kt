package com.drcorchit.justice.lang.members

import com.drcorchit.justice.lang.environment.Parameters
import com.drcorchit.justice.lang.types.Type

abstract class AbstractMember<T :  Any>(
    override val type: Type<in T>,
    override val name: String,
    override val description: String,
    override val parameters: Parameters,
    override val returnType: Type<*>,
    override val hasSideEffects: Boolean
) : Member<T> {
    override fun toString(): String {
        return "$type.$name"
    }
}