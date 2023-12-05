package com.drcorchit.justice.lang.types

import com.drcorchit.justice.game.Game
import com.drcorchit.justice.game.evaluation.universe.TypeUniverse
import com.drcorchit.justice.game.mechanics.GameMechanic
import com.google.gson.JsonElement
import com.google.gson.JsonPrimitive
import kotlin.reflect.KClass

object GameMechanicType : ReflectionType<GameMechanic<*>>(GameMechanic::class, TypeUniverse.getDefault())

class MechanicType<T : GameMechanic<*>>(clazz: KClass<T>, universe: TypeUniverse): ReflectionType<T>(clazz, universe, GameMechanicType) {
    override fun serialize(instance: T): JsonElement {
        return JsonPrimitive(instance.name)
    }

    override fun deserialize(game: Game, ele: JsonElement): T {
        return game.mechanics[ele.asString]
    }
}