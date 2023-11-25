package com.drcorchit.justice.lang.types

import com.drcorchit.justice.game.Game
import com.drcorchit.justice.lang.members.Member
import com.google.gson.JsonElement

object MemberType: ReflectionType<Member<*>>(Member::class) {

    override fun serialize(instance: Member<*>): JsonElement {
        throw UnsupportedOperationException()
    }

    override fun deserialize(game: Game, ele: JsonElement): Member<*> {
        throw UnsupportedOperationException()
    }
}