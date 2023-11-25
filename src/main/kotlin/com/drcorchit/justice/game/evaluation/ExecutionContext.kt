package com.drcorchit.justice.game.evaluation

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