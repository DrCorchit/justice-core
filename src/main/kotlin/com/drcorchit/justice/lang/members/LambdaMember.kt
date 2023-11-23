package com.drcorchit.justice.lang.members

import com.drcorchit.justice.lang.types.Type
import com.google.common.collect.ImmutableList

open class LambdaMember<T : Any>(
    clazz: Class<T>,
    name: String,
    description: String,
    argTypes: List<Type<*>>,
    returnType: Type<*>,
    hasSideEffects: Boolean,
    private val impl: (T, List<Any?>) -> Any?
) : AbstractMember<T>(clazz, name, description, ImmutableList.copyOf(argTypes), returnType, hasSideEffects) {
    override fun apply(instance: T, args: List<Any?>): Any? {
        return impl.invoke(instance, args)
    }
}