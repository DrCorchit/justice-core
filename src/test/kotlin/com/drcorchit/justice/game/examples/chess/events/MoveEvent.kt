package com.drcorchit.justice.game.examples.chess.events

import com.drcorchit.justice.exceptions.JusticeException
import com.drcorchit.justice.game.events.Events
import com.drcorchit.justice.game.examples.chess.Pieces
import com.drcorchit.justice.game.mechanics.space.Coordinate
import com.drcorchit.justice.game.players.Player
import com.drcorchit.justice.lang.annotations.HardcodedEvent

@HardcodedEvent("move", "Moves a piece from one square to another.")
class MoveEvent(val events: Events) {
    fun isAuthorized(author: Player, piece: Pieces.Piece, destination: Coordinate): Boolean {
        return piece.color.player == author
    }

    fun trigger(author: Player, piece: Pieces.Piece, destination: Coordinate) {
        if (piece.isMoveValid(destination)) {
            piece.position = destination
        } else {
            throw JusticeException("Invalid move.")
        }
    }
}