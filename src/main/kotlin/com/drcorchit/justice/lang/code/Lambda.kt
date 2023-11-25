package com.drcorchit.justice.lang.code

import com.drcorchit.justice.lang.environment.ImmutableTypeEnv
import com.drcorchit.justice.lang.members.LambdaMember
import com.drcorchit.justice.lang.members.Member
import com.drcorchit.justice.lang.types.NonSerializableType
import com.drcorchit.justice.lang.types.Type
import com.google.common.collect.ImmutableMap

class Lambda(
    private val parameters: ImmutableTypeEnv,
    private val returnType: Type<*>,
    private val impl: (List<Any>) -> Any
) {
    private val evaluator by lazy {
        getLambdaEvaluator(parameters, returnType)
    }

    fun getType(): Type<Lambda> {
        return evaluator
    }

    fun wrap(): Thing<Lambda> {
        return Thing(this, evaluator)
    }

    companion object {
        fun getLambdaEvaluator(parameters: ImmutableTypeEnv, returnType: Type<*>): Type<Lambda> {
            return object : NonSerializableType<Lambda>() {
                override val clazz = Lambda::class.java
                override val members: ImmutableMap<String, Member<Lambda>> = ImmutableMap.copyOf(listOf(
                    LambdaMember(
                        Lambda::class.java,
                        "invoke",
                        "Invokes the lambda.",
                        parameters.toArgs(),
                        returnType,
                        true
                    ) { instance, args -> instance.impl.invoke(args) }
                    //TODO args/return type could be added as members
                ).associateBy { it.name })
            }
        }
    }
}