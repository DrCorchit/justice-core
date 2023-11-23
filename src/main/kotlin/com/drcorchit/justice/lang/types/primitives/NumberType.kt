package com.drcorchit.justice.lang.types.primitives

import com.drcorchit.justice.game.Game
import com.drcorchit.justice.lang.types.Type
import com.drcorchit.justice.lang.members.Member
import com.google.common.collect.ImmutableMap
import com.google.gson.JsonElement
import com.google.gson.JsonPrimitive

object NumberType : Type<Number> {
    override val clazz = Number::class.java
    override val members: ImmutableMap<String, Member<Number>> = ImmutableMap.of()

    override fun serialize(instance: Number): JsonElement {
        return JsonPrimitive(instance)
    }

    override fun deserialize(game: Game, ele: JsonElement): Number {
        return ele.asNumber
    }
}