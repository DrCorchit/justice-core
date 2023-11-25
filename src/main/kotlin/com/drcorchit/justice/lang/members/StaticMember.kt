package com.drcorchit.justice.lang.members

import com.drcorchit.justice.lang.types.Type
import com.drcorchit.justice.lang.code.Thing
import com.google.common.collect.ImmutableList

abstract class StaticMember(name: String, description: String, argTypes: ImmutableList<Type<*>>, returnType: Type<*>) :
    AbstractMember<Unit>(Unit::class.java, name, description, argTypes, returnType, false) {

    fun apply(args: List<Any>): Any {
        return apply(Unit, args)
    }

    fun applyAndWrap(args: List<Any>): Thing<*> {
        return returnType.wrap(apply(Unit, args))
    }
}