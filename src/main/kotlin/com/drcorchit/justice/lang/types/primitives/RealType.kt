package com.drcorchit.justice.lang.types.primitives

import com.drcorchit.justice.exceptions.TypeException
import com.drcorchit.justice.game.Game
import com.drcorchit.justice.lang.members.Member
import com.drcorchit.justice.lang.types.Type
import com.google.common.collect.ImmutableMap
import com.google.gson.JsonElement
import com.google.gson.JsonPrimitive
import kotlin.reflect.full.isSuperclassOf

object RealType : Type<Double> {
    override val clazz = Double::class.java
    override val members: ImmutableMap<String, Member<Double>> = ImmutableMap.of()
    override val parent = NumberType

    override fun accept(other: Type<*>): Boolean {
        return Number::class.isSuperclassOf(other.clazz.kotlin)
    }

    override fun cast(instance: Any): Double {
        return if (instance is Number) {
            instance.toDouble()
        } else {
            throw TypeException("cast", clazz.name, instance::class.java.name)
        }
    }

    override fun serialize(instance: Double): JsonElement {
        return JsonPrimitive(instance)
    }

    override fun deserialize(game: Game, ele: JsonElement): Double {
        return ele.asDouble
    }
}