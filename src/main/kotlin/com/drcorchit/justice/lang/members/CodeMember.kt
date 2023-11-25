package com.drcorchit.justice.lang.members

import com.drcorchit.justice.exceptions.ReturnException
import com.drcorchit.justice.game.evaluation.Types
import com.drcorchit.justice.lang.code.Code
import com.drcorchit.justice.lang.environment.ImmutableTypeEnv
import com.drcorchit.justice.lang.code.Thing
import com.drcorchit.justice.lang.types.Type

class CodeMember<T : Any>(
    val type: Type<T>,
    name: String,
    description: String,
    argEnv: ImmutableTypeEnv,
    returnType: Type<*>,
    hasSideEffects: Boolean,
    val code: Code,
    val types: Types
) : AbstractMember<T>(type.clazz, name, description, argEnv.toArgs(), returnType, hasSideEffects) {

    override fun apply(instance: T, args: List<Any>): Any {
        return try {
            val context = types.getExecutionContext(hasSideEffects, Thing(instance, type))
            code.run(context)
        } catch (r: ReturnException) {
            r.value.value
        }
    }
}