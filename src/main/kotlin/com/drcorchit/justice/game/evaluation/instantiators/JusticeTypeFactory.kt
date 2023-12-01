package com.drcorchit.justice.game.evaluation.instantiators

import com.drcorchit.justice.game.evaluation.universe.TypeParameters
import com.drcorchit.justice.game.evaluation.universe.TypeUniverse
import com.drcorchit.justice.game.evaluation.universe.kClass
import com.drcorchit.justice.lang.annotations.EvaluableClass
import com.drcorchit.justice.lang.types.AnyType
import com.drcorchit.justice.lang.types.ReflectionType
import com.drcorchit.justice.lang.types.Type

class JusticeTypeFactory(val universe: TypeUniverse): TypeFactory(Any::class) {
    override fun instantiate(typeParameters: TypeParameters): Type<*> {
        val classAnnotation = typeParameters.kClass.annotations
            .filterIsInstance<EvaluableClass>().firstOrNull()
        val superclass = classAnnotation?.let { universe.parseType(it.supertype) } ?: AnyType
        return ReflectionType(typeParameters.kClass, universe, superclass)
    }
}