package com.drcorchit.justice.game.evaluation.instantiators

import com.drcorchit.justice.game.evaluation.universe.TypeParameters
import com.drcorchit.justice.game.evaluation.universe.kClass
import com.drcorchit.justice.lang.types.EnumType
import com.drcorchit.justice.lang.types.Type
import kotlin.reflect.KClass

object EnumTypeFactory : TypeFactory(Enum::class) {
    override fun instantiate(typeParameters: TypeParameters): Type<Enum<*>> {
        if (typeParameters.kClass.java.isEnum) {
            return EnumType(typeParameters.kClass as KClass<Enum<*>>)
        } else {
            throw IllegalArgumentException("Provided TypeParameters do not correspond to an enum class!")
        }
    }
}