package com.drcorchit.justice.lang.types.source

import com.drcorchit.justice.game.metadata.Metadata
import com.drcorchit.justice.game.metadata.MetadataType
import com.drcorchit.justice.game.players.Player
import com.drcorchit.justice.game.players.PlayerType
import com.drcorchit.justice.game.players.Players
import com.drcorchit.justice.game.players.PlayersType
import com.drcorchit.justice.lang.types.ArrayType
import com.drcorchit.justice.lang.types.Type
import com.drcorchit.justice.lang.types.primitives.*
import kotlin.reflect.KClass

interface TypeSource {
    fun typeOfInstance(instance: Any): Type<*> {
        return getType(instance::class)
    }

    fun getType(name: String): Type<*> {
        val kClass = Class.forName(name).kotlin
        return getType(kClass)
    }

    fun getType(kClass: KClass<*>): Type<*>

    companion object {
        val universe: ImmutableTypeSource

        init {
            val temp = MutableTypeSource()
            temp.registerType(Boolean::class) { _ -> BooleanType }
            temp.registerType(Int::class) { _ -> IntType }
            temp.registerType(Long::class) { _ -> LongType }
            temp.registerType(Double::class) { _ -> RealType }
            temp.registerType(String::class) { _ -> StringType }
            temp.registerType(Array::class) { _ -> ArrayType }
            temp.registerType(Player::class) { _ -> PlayerType }
            temp.registerType(Players::class) { _ -> PlayersType }
            temp.registerType(Metadata::class) { _ -> MetadataType }

            universe = temp.immutableCopy()
        }
    }
}