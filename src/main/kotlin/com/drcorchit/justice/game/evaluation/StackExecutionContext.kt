package com.drcorchit.justice.game.evaluation

import com.drcorchit.justice.lang.environment.Environment
import com.drcorchit.justice.lang.environment.MapEnvironment
import com.drcorchit.justice.lang.types.Thing
import com.drcorchit.justice.lang.types.Type
import java.util.*

class StackExecutionContext(
    override val universe: TypeUniverse,
    override val sideEffectsDisabled: Boolean,
    val self: Thing<*>? = null
) : ExecutionContext {
    private val global = MapEnvironment()
    private val stack: Stack<Environment> = Stack()

    init {
        push()
    }

    fun declareGlobal(id: String, value: Thing<*>) {
        global.declare(id, value.type, value.value, false)
    }

    override fun push() {
        stack.push(MapEnvironment())
    }

    override fun push(env: Environment) {
        stack.push(env as MapEnvironment)
    }

    override fun pop() {
        stack.pop()
    }

    override fun lookup(id: String): Thing<*>? {
        //First, check "self" keyword.
        if (id == "self" && self != null) return self
        //Second, check local vars
        val localVar = stack.firstOrNull { it.lookup(id) != null }?.lookup(id)
        return localVar ?:
        //Third, check members of self
            self?.getMember(id) ?:
        //Fourth, check global variables.
            global.lookup(id)
    }

    override fun declare(id: String, type: Type<*>, initialValue: Any?, mutable: Boolean) {
        stack.first().declare(id, type, initialValue, mutable)
    }

    override fun assign(id: String, value: Any) {
        stack.first().assign(id, value)
    }
}