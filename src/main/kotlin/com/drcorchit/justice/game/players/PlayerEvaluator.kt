package com.drcorchit.justice.game.players

import com.drcorchit.justice.lang.evaluators.Evaluator
import com.drcorchit.justice.lang.evaluators.StringEvaluator
import com.drcorchit.justice.lang.members.LambdaDataMember
import com.drcorchit.justice.lang.members.Member
import com.google.common.collect.ImmutableMap

object PlayerEvaluator : Evaluator<Player> {
    override val clazz = Player::class

    override val members: ImmutableMap<String, Member<Player>> = ImmutableMap.copyOf(listOf(
        LambdaDataMember<Player>(
            "id",
            "The unique identifier of the player.",
            listOf<Evaluator<*>>(),
            StringEvaluator
        ) { it.id }
    ).associateBy { it.name })

}