package com.drcorchit.justice.lang.members

import com.drcorchit.justice.exceptions.ReturnException
import com.drcorchit.justice.game.evaluation.Types
import com.drcorchit.justice.lang.code.Code
import com.drcorchit.justice.lang.code.Thing
import com.drcorchit.justice.game.evaluation.environment.Parameters
import com.drcorchit.justice.lang.types.Type

class InterpretedMember<T : Any>(
    type: Type<T>,
    name: String,
    description: String,
    parameters: Parameters,
    returnType: Type<*>,
    hasSideEffects: Boolean,
    val code: Code,
    val types: Types
) : AbstractMember<T>(type, name, description, parameters, returnType, hasSideEffects) {

    override fun apply(instance: T, args: List<Any>): Any {
        return try {
            val context = types.getExecutionContext(hasSideEffects, Thing(instance, type))
            code.run(context).value
        } catch (r: ReturnException) {
            r.value.value
        }
    }
}