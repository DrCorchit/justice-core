package com.drcorchit.justice.lang.members

import com.drcorchit.justice.lang.code.Thing
import com.drcorchit.justice.lang.environment.Parameters
import com.drcorchit.justice.lang.types.Type
import com.drcorchit.justice.lang.types.UnitType

abstract class StaticMember(name: String, description: String, argTypes: Parameters, returnType: Type<*>) :
    AbstractMember<Unit>(UnitType, name, description, argTypes, returnType, false) {

    fun apply(args: List<Any>): Any {
        return apply(Unit, args)
    }

    fun applyAndWrap(args: List<Any>): Thing<*> {
        return returnType.wrap(apply(Unit, args))
    }
}