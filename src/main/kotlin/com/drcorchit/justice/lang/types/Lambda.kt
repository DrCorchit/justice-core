package com.drcorchit.justice.lang.types

import com.drcorchit.justice.lang.environment.ImmutableTypeEnv
import com.drcorchit.justice.lang.members.LambdaMember
import com.drcorchit.justice.lang.members.Member
import com.google.common.collect.ImmutableMap

class Lambda(
    private val parameters: ImmutableTypeEnv,
    private val returnType: Type<*>,
    private val impl: (List<Any?>) -> Any?
) : HasType<Lambda> {
    private val evaluator by lazy {
        getLambdaEvaluator(parameters, returnType)
    }

    override fun getType(): Type<Lambda> {
        return evaluator
    }

    fun wrap(): TypedThing<Lambda> {
        return TypedThing(this, getType())
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
                    ) { instance, args ->
                        instance.impl.invoke(args)!!
                    }
                    //TODO args/return type could be added as members
                ).associateBy { it.name })
            }
        }
    }
}