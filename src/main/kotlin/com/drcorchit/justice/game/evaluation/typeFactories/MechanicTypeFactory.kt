package com.drcorchit.justice.game.evaluation.typeFactories

import com.drcorchit.justice.game.evaluation.universe.TypeParameters
import com.drcorchit.justice.game.evaluation.universe.TypeUniverse
import com.drcorchit.justice.game.evaluation.universe.kClass
import com.drcorchit.justice.game.mechanics.GameMechanic
import com.drcorchit.justice.lang.types.MechanicType
import com.drcorchit.justice.lang.types.Type
import kotlin.reflect.KClass


class MechanicTypeFactory(val universe: TypeUniverse) : TypeFactory(GameMechanic::class) {
    override fun instantiate(typeParameters: TypeParameters): Type<*> {
        return MechanicType(typeParameters.kClass as KClass<out GameMechanic<*>>, universe)
    }
}

