package com.drcorchit.justice.game.evaluation.instantiators

import com.drcorchit.justice.game.evaluation.universe.TypeParameters
import com.drcorchit.justice.game.evaluation.universe.params
import com.drcorchit.justice.lang.types.MapType
import com.drcorchit.justice.lang.types.Type

object MapTypeFactory: TypeFactory(Map::class) {
    override fun instantiate(typeParameters: TypeParameters): Type<*> {
        check(typeParameters.params.size == 2) { "A map requires a key and a value type!" }
        return MapType(typeParameters.params[0], typeParameters.params[1])
    }
}