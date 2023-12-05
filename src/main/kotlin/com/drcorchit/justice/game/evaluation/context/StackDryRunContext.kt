package com.drcorchit.justice.game.evaluation.context

import com.drcorchit.justice.game.evaluation.environment.MutableTypeEnv
import com.drcorchit.justice.game.evaluation.environment.TypeEnv
import com.drcorchit.justice.game.evaluation.universe.TypeUniverse
import com.drcorchit.justice.lang.types.MemberType
import com.drcorchit.justice.lang.types.Type
import java.util.*

class StackDryRunContext(
    override val universe: TypeUniverse,
    override val sideEffectsDisabled: Boolean,
    val self: Type<*>? = null
) : DryRunContext {
    private val global = MutableTypeEnv()
    private val stack: Stack<TypeEnv> = Stack()

    init {
        push()
    }

    override fun push() {
        stack.push(MutableTypeEnv())
    }

    override fun push(env: TypeEnv) {
        stack.push(env as MutableTypeEnv)
    }

    override fun pop() {
        stack.pop()
    }

    override fun lookup(id: String): Type<*>? {
        //First, check "self" keyword.
        if (id == "self" && self != null) return self
        //Second, check local vars
        return stack.firstOrNull { it.lookup(id) != null }?.lookup(id) ?:
        //Third, check members of self
            if (self?.getMember(id) != null) MemberType
        //Fourth, check global variables.
            else global.lookup(id)
    }

    fun declareGlobal(id: String, value: Type<*>) {
        global.declare(id, value, false)
    }

    override fun declare(id: String, type: Type<*>, mutable: Boolean) {
        stack.first().declare(id, type, mutable)
    }

    override fun assign(id: String, value: Type<*>) {
        stack.first().assign(id, value)
    }

    override fun toString(): String {
        return "{self: $self sideEffectsDisabled: $sideEffectsDisabled global: $global stack: $stack}"
    }
}