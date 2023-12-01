package com.drcorchit.justice.game.mechanics.space

import com.drcorchit.justice.exceptions.DeserializationException
import com.drcorchit.justice.game.Game
import com.drcorchit.justice.lang.members.Member
import com.drcorchit.justice.lang.members.lambda.LambdaFieldMember
import com.drcorchit.justice.lang.types.Type
import com.drcorchit.justice.lang.types.primitives.IntType
import com.drcorchit.justice.utils.logging.Uri.Companion.URI_REGEX
import com.drcorchit.justice.utils.math.Space
import com.google.common.collect.ImmutableMap
import com.google.gson.JsonElement
import com.google.gson.JsonPrimitive

object CoordinateType : Type<Coordinate>(Coordinate::class) {
    override val members: ImmutableMap<String, Member<Coordinate>> = listOf(
        LambdaFieldMember(this, "x", "The x position of the coordinate", IntType) { it.x },
        LambdaFieldMember(this, "y", "The y position of the coordinate", IntType) { it.y }
    ).toMemberMap()

    val regex1 = "($URI_REGEX)\\.((\\d+),(\\d+))".toRegex()

    //Coordinates without explicitly named space uses the default space.
    val regex2 = "((\\d+),(\\d+))".toRegex()

    override fun serialize(instance: Coordinate): JsonElement {
        return JsonPrimitive(instance.toString())
    }

    override fun deserialize(game: Game, ele: JsonElement): Coordinate {
        val str = ele.asString
        val spaces = game.mechanics.get<NamedSpaces>("spaces")
        return if (str.matches(regex1)) {
            val parts = regex1.matchEntire(str)!!.groupValues
            val space = spaces[parts[0]]
            val x = parts[1].toInt()
            val y = parts[2].toInt()
            space.getCoordinate(x, y)
        } else if (str.matches(regex2)) {
            val parts = Space.parseString(str)
            val space = spaces.defaultElement!!
            space.getCoordinate(parts.first, parts.second)
        } else {
            throw DeserializationException("Unable to deserialize coordinate. Invalid input string: $str")
        }
    }
}