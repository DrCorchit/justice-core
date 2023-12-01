package com.drcorchit.justice.lang.members

import com.drcorchit.justice.lang.annotations.DerivedField
import com.drcorchit.justice.lang.environment.Parameters
import com.drcorchit.justice.lang.types.Type

interface Member<in T : Any> {
    val type: Type<in T>
    @get:DerivedField("The name of the member.")
    val name: String
    @get:DerivedField("Describes the effect of invoking the member.")
    val description: String
    @get:DerivedField("The (possibly empty) argument types of the member.")
    val parameters: Parameters
    @get:DerivedField("The (possibly void) type returned by invoking the member.")
    val returnType: Type<*>
    @get:DerivedField("Marks the member as having potential side effects.")
    val hasSideEffects: Boolean

    fun apply(instance: T, args: List<Any>): Any
}