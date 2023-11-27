package com.drcorchit.justice.game.evaluation.instantiators

import com.drcorchit.justice.game.evaluation.universe.TypeParameters
import com.drcorchit.justice.game.evaluation.universe.TypeUniverse
import com.drcorchit.justice.game.evaluation.universe.kClass
import com.drcorchit.justice.game.mechanics.GameElement
import com.drcorchit.justice.lang.types.ElementType
import com.drcorchit.justice.lang.types.ReflectionType
import com.drcorchit.justice.lang.types.Type

class ElementTypeInstantiator(val universe: TypeUniverse) : TypeInstantiator(GameElement::class) {
    override fun instantiate(typeParameters: TypeParameters): Type<*> {
        return ReflectionType(typeParameters.kClass, universe, ElementType)
    }
}