package com.drcorchit.justice.game.evaluation

import com.drcorchit.justice.game.events.Event
import com.drcorchit.justice.game.metadata.Metadata
import com.drcorchit.justice.game.metadata.MetadataType
import com.drcorchit.justice.game.players.Player
import com.drcorchit.justice.game.players.PlayerType
import com.drcorchit.justice.game.players.Players
import com.drcorchit.justice.game.players.PlayersType
import com.drcorchit.justice.lang.JusticeLexer
import com.drcorchit.justice.lang.JusticeParser
import com.drcorchit.justice.lang.code.Visitor
import com.drcorchit.justice.lang.types.*
import com.drcorchit.justice.lang.types.primitives.*
import org.antlr.v4.runtime.CharStreams
import org.antlr.v4.runtime.CommonTokenStream
import kotlin.reflect.KClass

interface TypeUniverse {
    fun parseSimpleType(name: String): KClass<*>

    fun parseType(name: String): Type<*> {
        val lexer = JusticeLexer(CharStreams.fromString(name))
        val tokens = CommonTokenStream(lexer)
        val tree = JusticeParser(tokens)
        return Visitor(this)
            .handleType(tree.typeExpr())
            .resolveType(this)
    }

    fun getType(params: TypeParameters): Type<*>

    companion object {
        fun getDefault(): ImmutableTypeUniverse {
            return universe.immutableCopy()
        }
        
        private val universe: MutableTypeUniverse = MutableTypeUniverse()

        init {
            //Adding entries here allows a class to be evaluable without
            //Needing member annotations.
            universe.registerType(Unit::class) { _ -> UnitType }
            universe.registerType(Type::class) { _ -> TypeType }
            universe.registerType(Boolean::class) { _ -> BooleanType }
            universe.registerType(Int::class) { _ -> IntType }
            universe.registerType(Long::class) { _ -> LongType }
            universe.registerType(Double::class) { _ -> RealType }
            universe.registerType(Number::class) { _ -> NumberType }
            universe.registerType(String::class) { _ -> StringType }
            universe.registerType(Array::class) { classInfo -> ArrayType(classInfo.params.first()) }

            //ReflectionType singletons
            universe.registerType(Player::class) { _ -> PlayerType }
            universe.registerType(Players::class) { _ -> PlayersType }
            universe.registerType(Metadata::class) { _ -> MetadataType }
            universe.registerType(Event::class) { _ -> EventType }

            universe.registerName("Any", Any::class)
            universe.registerName("Null", Unit::class)
            universe.registerName("Bool", Boolean::class)
            universe.registerName("Int", Int::class)
            universe.registerName("Long", Long::class)
            universe.registerName("Real", Double::class)
            universe.registerName("String", String::class)
            universe.registerName("Type", Type::class)
            universe.registerName("Player", Player::class)
        }
    }
}