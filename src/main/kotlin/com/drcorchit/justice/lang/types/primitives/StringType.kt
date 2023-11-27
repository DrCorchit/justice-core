package com.drcorchit.justice.lang.types.primitives

import com.drcorchit.justice.game.Game
import com.drcorchit.justice.lang.members.Member
import com.drcorchit.justice.lang.types.Type
import com.drcorchit.justice.lang.types.Type.Companion.toMemberMap
import com.google.common.collect.ImmutableMap
import com.google.gson.JsonElement
import com.google.gson.JsonPrimitive

object StringType : Type<String> {
    override val clazz = String::class.java
    override val members: ImmutableMap<String, Member<String>> =
        //TODO
        listOf<Member<String>>(
            //ReflectionMember("length", String::class)
        ).toMemberMap()

    override fun serialize(instance: String): JsonElement {
        return JsonPrimitive(instance)
    }

    override fun deserialize(game: Game, ele: JsonElement): String {
        return ele.asString
    }
}