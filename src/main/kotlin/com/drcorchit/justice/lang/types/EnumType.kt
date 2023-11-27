package com.drcorchit.justice.lang.types

import com.drcorchit.justice.game.Game
import com.drcorchit.justice.lang.members.LambdaFieldMember
import com.drcorchit.justice.lang.members.Member
import com.drcorchit.justice.lang.types.Type.Companion.toMemberMap
import com.drcorchit.justice.lang.types.primitives.IntType
import com.drcorchit.justice.lang.types.primitives.StringType
import com.google.common.collect.ImmutableMap
import com.google.gson.JsonElement
import com.google.gson.JsonPrimitive

class EnumType(override val clazz: Class<Enum<*>>) : Type<Enum<*>> {
    override val members: ImmutableMap<String, Member<Enum<*>>> = listOf(
        LambdaFieldMember(
            Enum::class.java,
            "name",
            "The name of the enum object.",
            StringType
        ) { it.name },
        LambdaFieldMember(
            Enum::class.java,
            "ordinal",
            "The ordinal number assigned to the enum object",
            IntType
        ) { it.ordinal }
    ).toMemberMap()

    override fun serialize(instance: Enum<*>): JsonElement {
        return JsonPrimitive(instance.name)
    }

    override fun deserialize(game: Game, ele: JsonElement): Enum<*> {
        val desiredName = ele.asString
        return clazz.enumConstants?.first { it.name == desiredName }!!
    }
}