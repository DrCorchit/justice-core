package com.drcorchit.justice.game.evaluation.typeFactories

import com.drcorchit.justice.game.evaluation.universe.TypeParameters
import com.drcorchit.justice.game.evaluation.universe.kClass
import com.drcorchit.justice.game.evaluation.universe.params
import com.drcorchit.justice.lang.types.ArrayType
import com.drcorchit.justice.lang.types.Type
import kotlin.reflect.full.isSuperclassOf

object ArrayTypeFactory : TypeFactory(Array::class) {
    val arrayClasses = listOf(
        BooleanArray::class,
        ByteArray::class,
        CharArray::class,
        ShortArray::class,
        IntArray::class,
        LongArray::class,
        FloatArray::class,
        DoubleArray::class
    )

    override fun matches(typeParameters: TypeParameters): Boolean {
        return kClass.isSuperclassOf(typeParameters.kClass) ||
                arrayClasses.contains(typeParameters.kClass)
    }

    override fun instantiate(typeParameters: TypeParameters): Type<*> {
        val eleType = typeParameters.params.first()
        return ArrayType(eleType)
    }
}