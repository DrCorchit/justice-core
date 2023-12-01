package com.drcorchit.justice.lang.types

import com.drcorchit.justice.game.Game
import com.drcorchit.justice.lang.members.Member
import com.drcorchit.justice.lang.members.lambda.LambdaFieldMember
import com.drcorchit.justice.lang.types.primitives.IntType
import com.drcorchit.justice.lang.types.primitives.StringType
import com.google.common.collect.ImmutableMap
import com.google.gson.JsonElement
import com.google.gson.JsonPrimitive
import kotlin.reflect.KClass

class EnumType(clazz: KClass<Enum<*>>) : Type<Enum<*>>(clazz) {
    override val members: ImmutableMap<String, Member<Enum<*>>> = listOf(
        LambdaFieldMember(
            this,
            "name",
            "The name of the enum object.",
            StringType
        ) { it.name },
        LambdaFieldMember(
            this,
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
        return clazz.java.enumConstants?.first { it.name.equals(desiredName, true) }!!
    }
}