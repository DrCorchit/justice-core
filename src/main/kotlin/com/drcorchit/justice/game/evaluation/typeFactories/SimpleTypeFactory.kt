package com.drcorchit.justice.game.evaluation.typeFactories

import com.drcorchit.justice.game.evaluation.universe.TypeParameters
import com.drcorchit.justice.lang.types.Type

class SimpleTypeFactory(val type: Type<*>): TypeFactory(type.clazz) {
    override fun instantiate(typeParameters: TypeParameters): Type<*> {
        return type
    }
}