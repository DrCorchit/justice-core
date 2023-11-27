package com.drcorchit.justice.game.examples.chess

import com.drcorchit.justice.game.mechanics.AbstractElement
import com.drcorchit.justice.game.mechanics.AbstractMechanic
import com.drcorchit.justice.game.mechanics.Mechanics
import com.drcorchit.justice.lang.annotations.DataField
import com.drcorchit.justice.utils.data.Grid
import com.drcorchit.justice.utils.logging.Uri
import com.drcorchit.justice.utils.math.Space
import com.google.gson.JsonObject

class Pieces(parent: Mechanics) : AbstractMechanic<Pieces.Piece>(parent, "pieces") {

    val board = Grid<Piece>(8, 8)

    inner class Piece(uri: Uri) : AbstractElement(uri) {
        override val parent: Pieces get() = this@Pieces

        @DataField("Indicates which player controls the piece.", false)
        lateinit var color: Color
        @DataField("Indicates the type of piece.")
        lateinit var type: Type
        @DataField("The location of the piece on the board.")
        lateinit var position: Space.Coordinate
    }

    enum class Color {
        WHITE, BLACK
    }

    enum class Type {
        KING, QUEEN, ROOK, BISHOP, KNIGHT, PAWN
    }

    override fun create(uri: Uri, info: JsonObject): Piece {
        return Piece(uri)
    }
}