package com.drcorchit.justice.lang.types.primitives

import com.drcorchit.justice.exceptions.TypeException
import com.drcorchit.justice.game.Game
import com.drcorchit.justice.lang.members.Member
import com.drcorchit.justice.lang.types.Type
import com.google.common.collect.ImmutableMap
import com.google.gson.JsonElement
import com.google.gson.JsonPrimitive
import kotlin.reflect.full.isSuperclassOf

object LongType : Type<Long> {
    override val clazz = Long::class.java
    override val members: ImmutableMap<String, Member<Long>> = ImmutableMap.of()

    override fun accept(other: Type<*>): Boolean {
        return Number::class.isSuperclassOf(other.clazz.kotlin)
    }

    override fun cast(instance: Any): Long {
        return if (instance is Number) {
            instance.toLong()
        } else {
            throw TypeException("cast", clazz.name, instance::class.java.name)
        }
    }

    override fun serialize(instance: Long): JsonElement {
        return JsonPrimitive(instance)
    }

    override fun deserialize(game: Game, ele: JsonElement): Long {
        return ele.asLong
    }
}