package com.drcorchit.justice.lang.types.primitives

import com.drcorchit.justice.game.Game
import com.drcorchit.justice.lang.members.Member
import com.drcorchit.justice.lang.types.Type
import com.google.common.collect.ImmutableMap
import com.google.gson.JsonElement
import com.google.gson.JsonPrimitive

object BooleanType: Type<Boolean>(Boolean::class) {
    override val members: ImmutableMap<String, Member<Boolean>> = ImmutableMap.of()
    override fun serialize(instance: Boolean): JsonElement {
        return JsonPrimitive(instance)
    }

    override fun deserialize(game: Game, ele: JsonElement): Boolean {
        return ele.asBoolean
    }
}