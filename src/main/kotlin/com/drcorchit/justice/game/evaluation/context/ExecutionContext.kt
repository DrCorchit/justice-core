package com.drcorchit.justice.game.evaluation.context

import com.drcorchit.justice.game.evaluation.universe.TypeUniverse
import com.drcorchit.justice.lang.environment.Environment

interface ExecutionContext: Environment {
    val universe: TypeUniverse

    val sideEffectsDisabled: Boolean

    //Variables declared after a push (i.e., inside a loop/block)
    // will be removed after the stack is popped.
    fun push()

    fun push(env: Environment)

    fun pop()
}