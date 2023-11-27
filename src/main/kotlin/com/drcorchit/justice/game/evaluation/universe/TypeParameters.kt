package com.drcorchit.justice.game.evaluation.universe

import com.drcorchit.justice.lang.types.Type
import com.google.common.collect.ImmutableList
import kotlin.reflect.KClass

typealias TypeParameters = Pair<KClass<*>, ImmutableList<Type<*>>>

val TypeParameters.kClass get() = this.first
val TypeParameters.params get() = this.second