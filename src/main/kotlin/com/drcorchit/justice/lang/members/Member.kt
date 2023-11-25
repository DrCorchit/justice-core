package com.drcorchit.justice.lang.members

import com.drcorchit.justice.lang.annotations.DerivedField
import com.drcorchit.justice.lang.types.Type
import com.drcorchit.justice.lang.types.Thing
import com.google.common.collect.ImmutableList

interface Member<in T : Any> {
    val clazz: Class<in T>
    @get:DerivedField("The name of the member.")
    val name: String
    @get:DerivedField("Describes the effect of invoking the member.")
    val description: String
    @get:DerivedField("The (possibly empty) argument types of the member.")
    val argTypes: ImmutableList<Type<*>>
    @get:DerivedField("The (possibly void) type returned by invoking the member.")
    val returnType: Type<*>
    @get:DerivedField("Marks the member as having potential side effects.")
    val hasSideEffects: Boolean

    fun apply(instance: T, args: List<Any>): Any

    fun applyAndWrap(instance: T, args: List<Any>): Thing<*> {
        return returnType.wrap(apply(instance, args))
    }
}