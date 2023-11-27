package com.drcorchit.justice.game.evaluation.universe

import com.drcorchit.justice.game.evaluation.instantiators.ArrayTypeInstantiator
import com.drcorchit.justice.game.evaluation.instantiators.EnumTypeInstantiator
import com.drcorchit.justice.game.players.Player
import com.drcorchit.justice.game.players.PlayerType
import com.drcorchit.justice.lang.JusticeLexer
import com.drcorchit.justice.lang.JusticeParser
import com.drcorchit.justice.lang.code.Visitor
import com.drcorchit.justice.lang.types.EventType
import com.drcorchit.justice.lang.types.Type
import com.drcorchit.justice.lang.types.TypeType
import com.drcorchit.justice.lang.types.UnitType
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
        fun getDefault(): MutableTypeUniverse {
            val universe = MutableTypeUniverse()
            //Adding entries here allows a class to be evaluable without
            //having Justice class/member annotations.
            universe.registerType(UnitType)
            universe.registerType(TypeType)
            universe.registerType(BooleanType)
            universe.registerType(IntType)
            universe.registerType(LongType)
            universe.registerType(RealType)
            universe.registerType(NumberType)
            universe.registerType(StringType)
            universe.registerType(EnumTypeInstantiator)
            universe.registerType(ArrayTypeInstantiator)

            //ReflectionType singletons
            universe.registerType(PlayerType)
            universe.registerType(EventType)

            universe.registerName("Any", Any::class)
            universe.registerName("Null", Unit::class)
            universe.registerName("Bool", Boolean::class)
            universe.registerName("Int", Int::class)
            universe.registerName("Long", Long::class)
            universe.registerName("Real", Double::class)
            universe.registerName("String", String::class)
            universe.registerName("Type", Type::class)
            universe.registerName("Player", Player::class)
            return universe
        }
    }
}