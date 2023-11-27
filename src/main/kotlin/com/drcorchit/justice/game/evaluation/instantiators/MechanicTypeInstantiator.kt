package com.drcorchit.justice.game.evaluation.instantiators

import com.drcorchit.justice.game.evaluation.universe.TypeParameters
import com.drcorchit.justice.game.evaluation.universe.TypeUniverse
import com.drcorchit.justice.game.evaluation.universe.kClass
import com.drcorchit.justice.game.mechanics.GameMechanic
import com.drcorchit.justice.lang.types.MechanicType
import com.drcorchit.justice.lang.types.ReflectionType
import com.drcorchit.justice.lang.types.Type


class MechanicTypeInstantiator(val universe: TypeUniverse) : TypeInstantiator(GameMechanic::class) {
    override fun instantiate(typeParameters: TypeParameters): Type<*> {
        return ReflectionType(typeParameters.kClass, universe, MechanicType)
    }
}

