package com.drcorchit.justice.lang.code

import com.drcorchit.justice.game.evaluation.environment.Parameters
import com.drcorchit.justice.lang.members.Member
import com.drcorchit.justice.lang.members.lambda.LambdaMember
import com.drcorchit.justice.lang.types.NonSerializableType
import com.drcorchit.justice.lang.types.Type
import com.google.common.collect.ImmutableMap

class Lambda(
    private val parameters: Parameters,
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
        fun getLambdaEvaluator(parameters: Parameters, returnType: Type<*>): Type<Lambda> {
            return object : NonSerializableType<Lambda>(Lambda::class) {
                override val members: ImmutableMap<String, Member<Lambda>> = ImmutableMap.copyOf(listOf(
                    LambdaMember(
                        this,
                        "invoke",
                        "Invokes the lambda.", parameters,
                        returnType,
                        true
                    ) { instance, args -> instance.impl.invoke(args) }
                    //TODO args/return type could be added as members
                ).associateBy { it.name })
            }
        }
    }
}