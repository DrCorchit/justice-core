package com.drcorchit.justice.game.evaluation.typeFactories

import com.drcorchit.justice.game.evaluation.universe.TypeParameters
import com.drcorchit.justice.game.evaluation.universe.TypeUniverse
import com.drcorchit.justice.game.evaluation.universe.kClass
import com.drcorchit.justice.game.mechanics.GameElement
import com.drcorchit.justice.lang.types.ElementType
import com.drcorchit.justice.lang.types.Type
import kotlin.reflect.KClass

class ElementTypeFactory(val universe: TypeUniverse) : TypeFactory(GameElement::class) {
    override fun instantiate(typeParameters: TypeParameters): Type<*> {
        return ElementType(typeParameters.kClass as KClass<out GameElement>, universe)
    }
}