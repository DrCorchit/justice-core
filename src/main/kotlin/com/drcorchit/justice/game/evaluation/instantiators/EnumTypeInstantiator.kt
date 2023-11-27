package com.drcorchit.justice.game.evaluation.instantiators

import com.drcorchit.justice.game.evaluation.universe.TypeParameters
import com.drcorchit.justice.game.evaluation.universe.kClass
import com.drcorchit.justice.lang.types.EnumType
import com.drcorchit.justice.lang.types.Type

object EnumTypeInstantiator : TypeInstantiator(Enum::class) {
    override fun instantiate(typeParameters: TypeParameters): Type<Enum<*>> {
        if (typeParameters.kClass.java.isEnum) {
            val jClass = typeParameters.kClass.java as Class<Enum<*>>
            return EnumType(jClass)
        } else {
            throw IllegalArgumentException("Provided TypeParameters do not correspond to an enum class!")
        }
    }
}