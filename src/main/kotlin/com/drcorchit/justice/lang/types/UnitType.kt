package com.drcorchit.justice.lang.types

import com.drcorchit.justice.game.Game
import com.drcorchit.justice.lang.members.Member
import com.google.common.collect.ImmutableMap
import com.google.gson.JsonElement
import com.google.gson.JsonNull

object UnitType : Type<Unit> {
    override val clazz = Unit::class.java
    override val members: ImmutableMap<String, Member<Unit>> = ImmutableMap.of()

    override fun serialize(instance: Unit): JsonElement {
        return JsonNull.INSTANCE
    }

    override fun deserialize(game: Game, ele: JsonElement) {
        //No-Op -- return unit.
    }
}