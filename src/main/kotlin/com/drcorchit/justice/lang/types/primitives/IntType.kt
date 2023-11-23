package com.drcorchit.justice.lang.types.primitives

import com.drcorchit.justice.game.Game
import com.drcorchit.justice.lang.members.Member
import com.drcorchit.justice.lang.types.Type
import com.google.common.collect.ImmutableMap
import com.google.gson.JsonElement
import com.google.gson.JsonPrimitive

object IntType : Type<Int> {
    override val clazz = Int::class.java
    override val members: ImmutableMap<String, Member<Int>> = ImmutableMap.of()

    override fun serialize(instance: Int): JsonElement {
        return JsonPrimitive(instance)
    }

    override fun deserialize(game: Game, ele: JsonElement): Int {
        return ele.asInt
    }
}