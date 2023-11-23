package com.drcorchit.justice.lang.members

import com.drcorchit.justice.lang.types.Type
import com.google.common.collect.ImmutableList

abstract class AbstractMember<T :  Any>(
    override val clazz: Class<T>,
    override val name: String,
    override val description: String,
    override val argTypes: ImmutableList<Type<*>>,
    override val returnType: Type<*>,
    override val hasSideEffects: Boolean
) : Member<T>