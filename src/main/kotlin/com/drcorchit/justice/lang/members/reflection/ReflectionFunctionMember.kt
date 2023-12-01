package com.drcorchit.justice.lang.members.reflection

import com.drcorchit.justice.game.evaluation.universe.TypeUniverse
import com.drcorchit.justice.lang.annotations.Evaluable
import com.drcorchit.justice.lang.types.Type
import kotlin.reflect.KCallable

class ReflectionFunctionMember<T : Any>(types: TypeUniverse, type: Type<in T>, member: KCallable<*>, annotation: Evaluable) :
    ReflectionMember<T>(types, type, member, annotation.description, annotation.hasSideEffects)