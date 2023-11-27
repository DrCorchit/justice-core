package com.drcorchit.justice.lang.members

import com.drcorchit.justice.lang.annotations.JFunction
import com.drcorchit.justice.game.evaluation.universe.TypeUniverse
import kotlin.reflect.KCallable

class ReflectionFunctionMember<T : Any>(types: TypeUniverse, clazz: Class<T>, member: KCallable<*>, annotation: JFunction) :
    ReflectionMember<T>(types, clazz, member, annotation.description, annotation.hasSideEffects)