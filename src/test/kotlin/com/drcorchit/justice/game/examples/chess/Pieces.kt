package com.drcorchit.justice.game.examples.chess

import com.drcorchit.justice.game.examples.chess.Pieces.Type.*
import com.drcorchit.justice.game.mechanics.AbstractElement
import com.drcorchit.justice.game.mechanics.IndexedMechanic
import com.drcorchit.justice.game.mechanics.Mechanics
import com.drcorchit.justice.game.mechanics.space.Coordinate
import com.drcorchit.justice.game.mechanics.space.NamedSpaces
import com.drcorchit.justice.lang.annotations.DataField
import com.google.gson.JsonObject
import kotlin.math.abs

class Pieces(parent: Mechanics, name: String) : IndexedMechanic<Pieces.Piece>(parent, name) {
    val spaces get() = parent.get<NamedSpaces>("spaces")
    val space = spaces.defaultElement!!
    fun getPieceAt(coordinate: Coordinate): Piece? {
        return elementsByID.values.firstOrNull { it.position == coordinate }
    }

    @DataField(
        "Indicates the file of the last 2-tile pawn push, if any. The field is set to -1 is there is no such file.",
        true,
        "-1"
    )
    var enPassantFile: Int = -1

    @DataField("Indicates whose move it is to play.", true, "WHITE")
    lateinit var currentTurn: Colors.ChessColor

    @DataField("Records the number of moves since the last capture or pawn move.", true, "0")
    var drawingMoveCount: Int = 0

    @DataField("When drawingMoveCount reaches this number, the game is considered a draw.", false, "50")
    var drawingMoveMax: Int = 50

    inner class Piece(id: String) : AbstractElement(id) {
        override val parent: Pieces get() = this@Pieces

        @DataField("Indicates which player controls the piece.", false)
        lateinit var color: Colors.ChessColor

        @DataField("Indicates the type of piece.", false)
        lateinit var type: Type

        @DataField("The location of the piece on the board.")
        lateinit var position: Coordinate

        fun isMoveValid(destination: Coordinate): Boolean {
            //It's not a move.
            if (destination == position) return false
            //An allied piece is already there.
            val pieceAtDest = getPieceAt(destination)
            if (pieceAtDest?.color == color) return false

            val xDif = destination.x - position.x
            val yDif = destination.y - position.y
            val sameDiag = abs(xDif) == abs(yDif)
            val sameFile = xDif == 0
            val sameRank = yDif == 0

            //This allows pieces to move through each other...
            return when (type) {
                KING -> abs(xDif) <= 1 && abs(yDif) <= 1
                QUEEN -> sameRank || sameFile || sameDiag
                ROOK -> sameRank || sameFile
                BISHOP -> sameDiag
                KNIGHT -> abs(xDif * yDif) == 2
                PAWN -> {
                    val atSecondRank: Boolean = if (color.pawnDirection > 0) {
                        position.y == 1
                    } else {
                        position.y == space.space.height - 2
                    }

                    if (pieceAtDest == null) {
                        xDif == 0 && yDif == color.pawnDirection
                    } else {
                        abs(xDif) == 1 && yDif == color.pawnDirection
                    }
                }
            }
        }
    }

    enum class Type {
        KING, QUEEN, ROOK, BISHOP, KNIGHT, PAWN
    }

    override fun create(id: String, info: JsonObject): Piece {
        return Piece(id)
    }
}