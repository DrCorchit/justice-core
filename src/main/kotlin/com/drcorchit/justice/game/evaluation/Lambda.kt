package com.drcorchit.justice.game.evaluation

import com.drcorchit.justice.lang.environment.ImmutableTypeEnv
import com.drcorchit.justice.lang.evaluators.Evaluator
import com.drcorchit.justice.lang.evaluators.HasEvaluator
import com.drcorchit.justice.lang.evaluators.NonSerializableEvaluator
import com.drcorchit.justice.lang.members.LambdaMember
import com.drcorchit.justice.lang.members.Member
import com.google.common.collect.ImmutableMap

class Lambda(
    val parameters: ImmutableTypeEnv,
    val returnType: Evaluator<*>?,
    val impl: (List<Any>) -> Any?
) : HasEvaluator<Lambda> {
    private val evaluator by lazy {
        getLambdaEvaluator(parameters, returnType)
    }

    override fun getEvaluator(): Evaluator<Lambda> {
        return evaluator
    }

    companion object {
        fun getLambdaEvaluator(parameters: ImmutableTypeEnv, returnType: Evaluator<*>?): Evaluator<Lambda> {
            return object : NonSerializableEvaluator<Lambda>() {
                override val clazz = Lambda::class
                override val members: ImmutableMap<String, Member<Lambda>> = ImmutableMap.copyOf(listOf(
                    LambdaMember<Lambda>(
                        "invoke",
                        "Invokes the lambda.",
                        parameters.toArgs(),
                        returnType
                    ) { instance, args ->
                        instance.impl.invoke(args)
                    }
                    //TODO args/return type could be added as members
                ).associateBy { it.name })
            }
        }
    }
}