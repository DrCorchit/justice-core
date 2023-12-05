package com.drcorchit.justice.lang.types

import com.drcorchit.justice.exceptions.DeserializationException
import com.drcorchit.justice.game.Game
import com.drcorchit.justice.game.evaluation.universe.TypeUniverse
import com.drcorchit.justice.game.mechanics.GameElement
import com.drcorchit.justice.game.mechanics.GameMechanic
import com.google.gson.JsonElement
import com.google.gson.JsonPrimitive
import kotlin.reflect.KClass

object GameElementType : ReflectionType<GameElement>(GameElement::class)

class ElementType<T : GameElement>(clazz: KClass<T>, universe: TypeUniverse): ReflectionType<T>(clazz, universe, GameElementType) {
    override fun serialize(instance: T): JsonElement {
        return JsonPrimitive("${instance.parent.name}.${instance.id}")
    }

    //We allow commas for the sake of GridElements
    private val elementRegex = "([a-zA-Z0-9_]+)\\.([a-zA-Z0-9,_]+)".toRegex()

    override fun deserialize(game: Game, ele: JsonElement): T {
        val match = elementRegex.matchEntire(ele.asString)?.groupValues
            ?: throw DeserializationException("Cannot deserialize element: $ele is not parsable.")
        val parent = match[1]
        val id = match[2]
        return game.mechanics.get<GameMechanic<T>>(parent)[id]
    }
}