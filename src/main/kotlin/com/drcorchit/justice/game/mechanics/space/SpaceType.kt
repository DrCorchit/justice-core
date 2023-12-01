package com.drcorchit.justice.game.mechanics.space

import com.drcorchit.justice.game.Game
import com.drcorchit.justice.lang.members.Member
import com.drcorchit.justice.lang.members.lambda.LambdaFieldMember
import com.drcorchit.justice.lang.types.Type
import com.drcorchit.justice.lang.types.primitives.IntType
import com.drcorchit.justice.utils.math.Space
import com.google.common.collect.ImmutableMap
import com.google.gson.JsonElement

object SpaceType : Type<Space>(Space::class) {
    override val members: ImmutableMap<String, Member<Space>> = listOf<Member<Space>>(
        LambdaFieldMember(this, "width", "The width of the underlying space.", IntType) { it.width },
        LambdaFieldMember(this, "height", "The height of the underlying space.", IntType) { it.height }
    ).toMemberMap()

    override fun serialize(instance: Space): JsonElement {
        return instance.serialize()
    }

    override fun deserialize(game: Game, ele: JsonElement): Space {
        return Space.deserialize(ele)
    }
}