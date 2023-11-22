package com.drcorchit.justice.lang.members

import com.drcorchit.justice.lang.evaluators.Evaluator
import com.google.common.collect.ImmutableList

abstract class AbstractMember<T :  Any>(
    override val name: String,
    override val description: String,
    override val argTypes: ImmutableList<Evaluator<*>>,
    override val returnType: Evaluator<*>?
) : Member<T>