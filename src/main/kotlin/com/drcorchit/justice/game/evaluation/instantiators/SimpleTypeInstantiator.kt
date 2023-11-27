package com.drcorchit.justice.game.evaluation.instantiators

import com.drcorchit.justice.game.evaluation.universe.TypeParameters
import com.drcorchit.justice.lang.types.Type

class SimpleTypeInstantiator(val type: Type<*>): TypeInstantiator(type.clazz.kotlin) {
    override fun instantiate(typeParameters: TypeParameters): Type<*> {
        return type
    }
}