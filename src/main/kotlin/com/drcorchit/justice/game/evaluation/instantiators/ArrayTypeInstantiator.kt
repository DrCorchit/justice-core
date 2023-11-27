package com.drcorchit.justice.game.evaluation.instantiators

import com.drcorchit.justice.game.evaluation.universe.TypeParameters
import com.drcorchit.justice.game.evaluation.universe.params
import com.drcorchit.justice.lang.types.ArrayType
import com.drcorchit.justice.lang.types.Type

object ArrayTypeInstantiator: TypeInstantiator(Array::class) {
    override fun instantiate(typeParameters: TypeParameters): Type<*> {
        val eleType = typeParameters.params.first()
        return ArrayType(eleType)
    }
}