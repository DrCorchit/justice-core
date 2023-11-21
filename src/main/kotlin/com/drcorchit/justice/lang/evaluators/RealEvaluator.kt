package com.drcorchit.justice.lang.evaluators

import com.drcorchit.justice.game.Game
import com.drcorchit.justice.lang.members.Member
import com.google.common.collect.ImmutableMap
import com.google.gson.JsonElement
import com.google.gson.JsonPrimitive

object RealEvaluator : Evaluator<Double> {
    override val clazz = Double::class
    override val members: ImmutableMap<String, Member<Double>> = ImmutableMap.of()

    override fun serialize(instance: Double): JsonElement {
        return JsonPrimitive(instance)
    }

    override fun deserialize(game: Game, ele: JsonElement): Double {
        return ele.asDouble
    }
}