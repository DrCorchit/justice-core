package com.drcorchit.justice.lang.types

import com.drcorchit.justice.game.Game
import com.drcorchit.justice.game.mechanics.GameElement
import com.drcorchit.justice.game.mechanics.GameMechanic
import com.drcorchit.justice.lang.types.source.TypeSource
import com.drcorchit.justice.utils.logging.Uri
import com.google.gson.JsonElement
import com.google.gson.JsonPrimitive
import kotlin.reflect.KClass

class ElementType<T: GameElement>(types: TypeSource, clazz: KClass<T>): ReflectionType<T>(types, clazz) {
    override fun serialize(instance: T): JsonElement {
        return JsonPrimitive(instance.uri.toString())
    }

    override fun deserialize(game: Game, ele: JsonElement): T {
        val uri = Uri.parse(ele.asString)
        return game.mechanics.get<GameMechanic<T>>(uri.parent!!.value)[uri.value]
    }
}